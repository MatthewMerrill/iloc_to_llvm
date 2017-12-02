package io.javalanche.covfefe.instructions;

import static org.bytedeco.javacpp.LLVM.LLVMBuildBr;
import static org.bytedeco.javacpp.LLVM.LLVMValueAsBasicBlock;

import io.javalanche.covfefe.CompileContext;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

public class InstructionBranch {

  static void branch(CompileContext ctx, String instruction) {
    LLVMValueRef[] args = InstructionEmitter.parse(ctx, "br -> %l", instruction);
    LLVMBuildBr(ctx.builderRef, LLVMValueAsBasicBlock(args[0]));
  }
}
