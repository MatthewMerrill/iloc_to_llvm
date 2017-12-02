package io.javalanche.covfefe.instructions;

import io.javalanche.covfefe.CompileContext;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

import static org.bytedeco.javacpp.LLVM.*;

public class InstructionArithmetic {

  public static void add(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "add %r, %r -> %r",
        instruction);
    LLVMValueRef register1 = LLVMBuildLoad(ctx.builderRef, res[0], "add_r1_");
    LLVMValueRef register2 = LLVMBuildLoad(ctx.builderRef, res[1], "add_r2_");
    LLVMValueRef ref = LLVMBuildAdd(ctx.builderRef, register1, register2, "add");
    res[2] = LLVMBuildStore(ctx.builderRef, ref, res[2]);
  }

  public static void sub(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "sub %r, %r -> %r",
        instruction);
    LLVMValueRef register1 = LLVMBuildLoad(ctx.builderRef, res[0], "sub_r1_");
    LLVMValueRef register2 = LLVMBuildLoad(ctx.builderRef, res[1], "sub_r2_");
    LLVMValueRef ref = LLVMBuildSub(ctx.builderRef, register1, register2, "sub");
    res[2] = LLVMBuildStore(ctx.builderRef, ref, res[2]);
  }

  public static void mul(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "sub %r, %r -> %r",
        instruction);
    LLVMValueRef register1 = LLVMBuildLoad(ctx.builderRef, res[0], "mul_r1_");
    LLVMValueRef register2 = LLVMBuildLoad(ctx.builderRef, res[1], "mul_r2_");
    LLVMValueRef ref = LLVMBuildMul(ctx.builderRef, register1, register2, "mul");
    res[2] = LLVMBuildStore(ctx.builderRef, ref, res[2]);
  }

  public static void div(CompileContext ctx, String instruction) {
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "sub %r, %r -> %r",
        instruction);
    LLVMValueRef register1 = LLVMBuildLoad(ctx.builderRef, res[0], "div_r1_");
    LLVMValueRef register2 = LLVMBuildLoad(ctx.builderRef, res[1], "div_r2_");
    LLVMValueRef ref = LLVMBuildSDiv(ctx.builderRef, register1, register2, "div");
    res[2] = LLVMBuildStore(ctx.builderRef, ref, res[2]);
  }

}
