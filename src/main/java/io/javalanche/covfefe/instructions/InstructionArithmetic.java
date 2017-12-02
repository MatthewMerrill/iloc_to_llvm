package io.javalanche.covfefe.instructions;

import io.javalanche.covfefe.CompileContext;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.nashorn.internal.objects.annotations.Function;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

import static org.bytedeco.javacpp.LLVM.*;

class InstructionArithmetic {

  static void add(CompileContext ctx, String instruction) {
    buildArtithmetic(ctx, "add %r, %r -> %r", instruction, LLVM::LLVMBuildAdd, "add");
  }

  static void sub(CompileContext ctx, String instruction) {
    buildArtithmetic(ctx, "sub %r, %r -> %r", instruction, LLVM::LLVMBuildSub, "sub");
  }

  static void mul(CompileContext ctx, String instruction) {
    buildArtithmetic(ctx, "mul %r, %r -> %r", instruction, LLVM::LLVMBuildMul, "mul");
  }

  static void div(CompileContext ctx, String instruction) {
    buildArtithmetic(ctx, "div %r, %r -> %r", instruction, LLVM::LLVMBuildSDiv, "div");
  }

  @FunctionalInterface
  private interface LLVMArithmeticOp {
    LLVMValueRef apply(LLVMBuilderRef builderRef, LLVMValueRef a, LLVMValueRef b, String arg);
  }

  private static void buildArtithmetic(CompileContext ctx, String p, String instr,
      LLVMArithmeticOp op, String opName) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx, p, instr);
    LLVMValueRef register1 = LLVMBuildLoad(ctx.builderRef, res[0], opName + "_r1_");
    LLVMValueRef register2 = LLVMBuildLoad(ctx.builderRef, res[1], opName + "_r2_");
    LLVMValueRef ref = op.apply(ctx.builderRef, register1, register2, opName );
    res[2] = LLVMBuildStore(ctx.builderRef, ref, res[2]);

  }

}
