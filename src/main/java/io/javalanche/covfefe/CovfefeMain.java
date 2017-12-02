package io.javalanche.covfefe;

import io.javalanche.covfefe.instructions.InstructionEmitter;
import io.javalanche.covfefe.instructions.NopInstruction;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.LLVM.*;

public class CovfefeMain {

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.err.println("Provide iloc file as first arg!");
      System.exit(1);
    }

    Iterator<String> itr = Files.readAllLines(Paths.get(args[0]))
        .iterator();

    CompileContext ctx = parse(itr);
    runModule(ctx.moduleRef);
  }

  public static CompileContext parse(Iterator<String> iterator) {
    LLVMModuleRef moduleRef = LLVMModuleCreateWithName("my_module");
    LLVMBuilderRef builderRef = LLVMCreateBuilder();
    CompileContext ctx = new CompileContext(moduleRef, builderRef);

    while (iterator.hasNext()) {
      String instruction = iterator.next();

      InstructionEmitter emitter = InstructionEmitter.emitterMap.get(instruction.split(" ")[0]);
      System.out.println(Arrays.toString(emitter.getClass().getAnnotations()));
      emitter.sink(ctx, instruction);
    }

    return ctx;
  }

  public static void runModule(LLVMModuleRef moduleRef) {
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
  }
}
