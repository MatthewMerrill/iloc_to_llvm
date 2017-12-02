import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.bytedeco.javacpp.LLVM.*;

import io.javalanche.covfefe.CompileContext;
import io.javalanche.covfefe.instructions.InstructionEmitter;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;
import org.junit.Test;

public class TestInstructionParsing {

  @Test
  public void parseNop() {
    CompileContext ctx = new CompileContext(LLVM.LLVMModuleCreateWithName("test_mod"), null);
    LLVMValueRef[] res = InstructionEmitter.parse(ctx, "nop", "nop");
    assertThat(res).hasLength(0);
  }

  @Test
  public void parseAdd() {
    CompileContext ctx = new CompileContext(LLVM.LLVMModuleCreateWithName("test_mod"), null);
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "add %r, %r -> %r",
        "add r1, r2 -> r3");
    assertThat(res).hasLength(3);
    assertWithMessage("r1 is register")
        .that(LLVM.LLVMGetValueKind(res[0])).isEqualTo(LLVMGlobalVariableValueKind);
    assertWithMessage("r2 is register")
        .that(LLVM.LLVMGetValueKind(res[1])).isEqualTo(LLVMGlobalVariableValueKind);
    assertWithMessage("r3 is register")
        .that(LLVM.LLVMGetValueKind(res[2])).isEqualTo(LLVMGlobalVariableValueKind);
  }

  @Test
  public void parseAddI() {
    CompileContext ctx = new CompileContext(LLVM.LLVMModuleCreateWithName("test_mod"), null);
    LLVMValueRef[] res = InstructionEmitter.parse(ctx,
        "addI %r, %c -> %r",
        "addI r1, 2 -> r3");
    assertThat(res).hasLength(3);
    assertWithMessage("r1 is register")
        .that(LLVM.LLVMGetValueKind(res[0])).isEqualTo(LLVMGlobalVariableValueKind);
    assertWithMessage("c is constant")
        .that(LLVM.LLVMGetValueKind(res[1])).isEqualTo(LLVMConstantIntValueKind);
    assertWithMessage("r3 is register")
        .that(LLVM.LLVMGetValueKind(res[2])).isEqualTo(LLVMGlobalVariableValueKind);
  }
}
