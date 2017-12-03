package io.javalanche.covfefe.instructions;

import static org.bytedeco.javacpp.LLVM.LLVMBuildLoad;
import static org.bytedeco.javacpp.LLVM.LLVMBuildStore;

import io.javalanche.covfefe.CompileContext;
import io.javalanche.covfefe.MemoryManager;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

public class InstructionLoad {

  public static void loadi(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "loadi %c -> %r", instruction);
    LLVMBuildStore(ctx.builderRef, args[0], args[1]);
  }

  public static void loadAI(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "loadAI rarp, %c -> %r", instruction);
    LLVMBuildStore(ctx.builderRef, MemoryManager.loadMem(ctx, args[0]), args[1]);
  }
}
