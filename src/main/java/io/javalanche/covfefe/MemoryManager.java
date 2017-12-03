package io.javalanche.covfefe;

import static org.bytedeco.javacpp.LLVM.*;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;
import org.bytedeco.javacpp.PointerPointer;

public class MemoryManager {

  public static LLVMValueRef mem;

  public static void init(CompileContext ctx, int bytes) {
    mem = LLVMBuildAlloca(ctx.builderRef, LLVMArrayType(LLVMInt32Type(), bytes), "MEM");

//    LLVMBuildStore(ctx.builderRef, LLVMConstInt(LLVMIntType(bytes), 0, 0),
//        LLVMBuildBitCast(ctx.builderRef,
//            LLVMBuildGEP(ctx.builderRef, mem, LLVMConstInt(LLVMInt32Type(), 0, 0), 1,
//                new BytePointer("")),
//            LLVMPointerType(LLVMIntType(bytes), 0), ""));
  }

  public static LLVMValueRef loadMem(CompileContext ctx, LLVMValueRef idx) {
//    return LLVMBuildLoad(ctx.builderRef, LLVMBuildBitCast(ctx.builderRef,
//        LLVMBuildGEP(ctx.builderRef, mem, idx, 1, new BytePointer("")),
//        LLVMPointerType(LLVMInt32Type(), 0), ""), "");
    return LLVMBuildLoad(ctx.builderRef, LLVMBuildGEP(ctx.builderRef, mem, new PointerPointer<>(
        LLVMConstInt(LLVMInt32Type(), 0, 0),
        idx
    ), 2, ""), "");
  }

}
