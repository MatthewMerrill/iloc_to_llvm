package io.javalanche.covfefe.instructions;

import static org.bytedeco.javacpp.LLVM.*;

import io.javalanche.covfefe.CompileContext;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

class InstructionBranch {

  static void branch(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "(?:br|jumpi) -> %l", instruction);
    LLVMBuildBr(ctx.builderRef, LLVMValueAsBasicBlock(args[0]));
  }

  static void condition(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "cbr %r -> %l, %l", instruction);
    LLVMValueRef value = LLVMBuildLoad(ctx.builderRef, args[0], "cbr");
    LLVMBuildCondBr(ctx.builderRef,
        LLVMBuildICmp(ctx.builderRef, LLVMIntNE, value, LLVMConstInt(LLVMInt32Type(), 0, 0), ""),
        LLVMValueAsBasicBlock(args[1]), LLVMValueAsBasicBlock(args[2]));
  }
}
