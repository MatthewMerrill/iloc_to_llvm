package io.javalanche.covfefe.instructions;

import static org.bytedeco.javacpp.LLVM.*;

import io.javalanche.covfefe.CompileContext;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

class InstructionComparison {

  static void and(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx, "and %r, %r -> %r", instruction);
    LLVMValueRef leftside = LLVMBuildLoad(ctx.builderRef, res[0], "and_r1_");
    LLVMValueRef rightside = LLVMBuildLoad(ctx.builderRef, res[1], "and_r2_");
    LLVMValueRef ref = LLVMBuildAnd(ctx.builderRef, leftside, rightside, "and_r2");
    res[2] = LLVMBuildStore(ctx.builderRef,
        LLVMBuildIntCast(ctx.builderRef, ref, LLVMInt32Type(), ""), res[2]);
  }

  static void or(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx, "or %r, %r -> %r", instruction);
    LLVMValueRef leftside = LLVMBuildLoad(ctx.builderRef, res[0], "or_r1_");
    LLVMValueRef rightside = LLVMBuildLoad(ctx.builderRef, res[1], "or_r2_");
    LLVMValueRef ref = LLVMBuildOr(ctx.builderRef, leftside, rightside, "or_r2");
    res[2] = LLVMBuildStore(ctx.builderRef,
        LLVMBuildIntCast(ctx.builderRef, ref, LLVMInt32Type(), ""), res[2]);
  }

  static void cmp_eq(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_EQ %r, %r -> %r", instruction, LLVMIntEQ, "cmp_EQ");
  }

  static void cmp_ne(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_NE %r, %r -> %r", instruction, LLVMIntNE, "cmp_NE");
  }

  static void cmp_le(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_LE %r, %r -> %r", instruction, LLVMIntSLE, "cmp_LE");
  }

  static void cmp_ge(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_GE %r, %r -> %r", instruction, LLVMIntSGE, "cmp_GE");
  }

  static void cmp_lt(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_LT %r, %r -> %r", instruction, LLVMIntSLT, "cmp_LT");
  }

  static void cmp_gt(CompileContext ctx, String instruction) {
    buildComparison(ctx, "cmp_GT %r, %r -> %r", instruction, LLVMIntSGT, "cmp_GT");
  }

  private static void buildComparison(CompileContext ctx, String p, String instruction,
      int op, String opName) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx, p, instruction);
    LLVMValueRef leftside = LLVMBuildLoad(ctx.builderRef, res[0], opName + "_r1_");
    LLVMValueRef rightside = LLVMBuildLoad(ctx.builderRef, res[1], opName + "_r2_");
    LLVMValueRef ref = LLVMBuildICmp(ctx.builderRef, op, leftside, rightside, opName);
    res[2] = LLVMBuildStore(ctx.builderRef,
        LLVMBuildIntCast(ctx.builderRef, ref, LLVMInt32Type(), ""), res[2]);
  }
}
