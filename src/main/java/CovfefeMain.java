import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.LLVM.*;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.LLVM.*;

public class CovfefeMain {

  public static void main(String[] args) {
    LLVMModuleRef moduleRef = LLVMModuleCreateWithName("my_module");
    runModule(moduleRef);
  }

  public static void runModule(LLVMModuleRef moduleRef) {
    System.err.println("Your iloc as LLVM:");
    LLVMDumpModule(moduleRef);

    BytePointer error = new BytePointer((Pointer) null);
    LLVMInitializeNativeAsmPrinter();
    LLVMInitializeNativeAsmParser();
    LLVMInitializeNativeDisassembler();
    LLVMInitializeNativeTarget();

    LLVMVerifyModule(moduleRef, LLVMAbortProcessAction, error);

    LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
    if (LLVMCreateJITCompilerForModule(engine, moduleRef, 2, error) != 0) {
      System.err.println(error.getString());
      LLVMDisposeMessage(error);
      System.exit(-1);
    }

    LLVMPassManagerRef pass = LLVMCreatePassManager();
    LLVMAddConstantPropagationPass(pass);
    LLVMAddInstructionCombiningPass(pass);
    LLVMAddPromoteMemoryToRegisterPass(pass);
    LLVMAddDemoteMemoryToRegisterPass(pass); // Demotes every possible value to memory
    LLVMAddGVNPass(pass);
    LLVMAddCFGSimplificationPass(pass);
    LLVMRunPassManager(pass, moduleRef);
  }
}
