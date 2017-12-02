package io.javalanche.covfefe;

import static org.bytedeco.javacpp.LLVM.*;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

public class MemoryManager {

  private static LLVMValueRef mem;

  public void init(CompileContext ctx, int bytes) {
    mem = LLVMAddGlobal(ctx.moduleRef, LLVMArrayType(LLVMInt8Type(), bytes), "MEM");
  }

  public LLVMValueRef loadMem(CompileContext ctx, LLVMValueRef idx) {
    return LLVMBuildLoad(ctx.builderRef,
        LLVMBuildGEP(ctx.builderRef, mem, idx, 1, new BytePointer("")), "memLoad");
  }

}
