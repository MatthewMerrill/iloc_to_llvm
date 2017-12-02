package io.javalanche.covfefe;

import io.javalanche.covfefe.instructions.InstructionEmitter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;

import static org.bytedeco.javacpp.LLVM.*;

public class CovfefeMain {

  public static final Map<String, LLVMBasicBlockRef> blocks = new HashMap<>();

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.err.println("Provide iloc file as first arg!");
      System.exit(1);
    }

    List<String> lines = Files.readAllLines(Paths.get(args[0]));
    CompileContext ctx = parse(lines);

    runModule(ctx);
  }

  public static CompileContext parse(List<String> lines) {
    LLVMModuleRef moduleRef = LLVMModuleCreateWithName("my_module");
    LLVMBuilderRef builderRef = LLVMCreateBuilder();
    CompileContext ctx = new CompileContext(moduleRef, builderRef);

    LLVMValueRef main = LLVMAddFunction(moduleRef, "main",
        LLVMFunctionType(LLVMVoidType(), new PointerPointer<>(new LLVMTypeRef[0]), 0, 0));

    LLVMBasicBlockRef mainBlock = LLVMAppendBasicBlock(main, "");

    lines.stream()
        .filter(s -> s.trim().matches("\\w+:"))
        .map(label -> label.substring(0, label.length() - 1))
        .forEach(label -> blocks.put(label, LLVMAppendBasicBlock(main, label)));

    LLVMPositionBuilderAtEnd(ctx.builderRef, mainBlock);

    for (String instruction : lines) {
      instruction = instruction.trim();
      if (instruction.matches("\\w+:")) {
        LLVMBasicBlockRef blockRef = blocks.get(instruction.substring(0, instruction.length() - 1));
        if (LLVMGetBasicBlockTerminator(LLVMGetInsertBlock(ctx.builderRef)) == null) {
          LLVMBuildBr(ctx.builderRef, blockRef);
        }
        LLVMPositionBuilderAtEnd(ctx.builderRef, blockRef);
      }
      else if (!instruction.isEmpty()) {
        InstructionEmitter emitter = InstructionEmitter.emitterMap.get(instruction.split(" ")[0]);
        if (emitter == null) {
          System.out.printf("I don't know how to emit \"%s\"", instruction);
        }
        emitter.sink(ctx, instruction);
      }
    }

    LLVMBuildRetVoid(ctx.builderRef);

    return ctx;
  }

  public static void runModule(CompileContext ctx) {
    LLVMModuleRef moduleRef = ctx.moduleRef;

    System.err.println("Your iloc as LLVM:");
    LLVMDumpModule(moduleRef);

    BytePointer error = new BytePointer((Pointer) null);
    LLVMInitializeNativeAsmPrinter();
    LLVMInitializeNativeAsmParser();
    LLVMInitializeNativeDisassembler();
    LLVMInitializeNativeTarget();

    LLVMVerifyModule(moduleRef, LLVMAbortProcessAction, error);

    LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
    if (LLVMCreateJITCompilerForModule(engine, moduleRef, 2, error) != 0) {
      System.err.println(error.getString());
      LLVMDisposeMessage(error);
      System.exit(-1);
    }

    LLVMPassManagerRef pass = LLVMCreatePassManager();
    LLVMAddConstantPropagationPass(pass);
    LLVMAddInstructionCombiningPass(pass);
    LLVMAddPromoteMemoryToRegisterPass(pass);
    LLVMAddDemoteMemoryToRegisterPass(pass); // Demotes every possible value to memory
    LLVMAddGVNPass(pass);
    LLVMAddCFGSimplificationPass(pass);
    LLVMRunPassManager(pass, moduleRef);

    LLVMGenericValueRef exec_res = LLVMRunFunction(engine,
        LLVMGetNamedFunction(moduleRef, "main"), 0,
        (LLVMGenericValueRef) null);

  }
}
