package io.javalanche.covfefe.instructions;

import static io.javalanche.covfefe.MemoryManager.mem;
import static org.bytedeco.javacpp.LLVM.*;

import io.javalanche.covfefe.CompileContext;
import io.javalanche.covfefe.MemoryManager;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;
import org.bytedeco.javacpp.PointerPointer;

public class InstructionStore {

//  public static void loadi(CompileContext ctx, String instruction) {
//    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "loadi %c -> %r", instruction);
//    LLVMBuildStore(ctx.builderRef, args[0], args[1]);
//  }

  public static List<Long> storedIntegers = new ArrayList<>();

  public static void storeAI(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "storeAI %r -> rarp, %c", instruction);
    storedIntegers.add(LLVMConstIntGetSExtValue(args[1]));
    LLVMBuildStore(ctx.builderRef, LLVMBuildLoad(ctx.builderRef, args[0], ""),
        LLVMBuildGEP(ctx.builderRef, mem, new PointerPointer<>(
            LLVMConstInt(LLVMInt32Type(), 0, 0),
            args[1]
        ), 2, ""));
  }
}
