package io.javalanche.covfefe;

import static org.bytedeco.javacpp.LLVM.LLVMAddGlobal;
import static org.bytedeco.javacpp.LLVM.LLVMInt16Type;
import static org.bytedeco.javacpp.LLVM.LLVMInt32Type;

import java.util.HashMap;
import java.util.Map;
import org.bytedeco.javacpp.LLVM.LLVMModuleRef;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

public class RegisterReferences {

  private static final Map<String, LLVMValueRef> registerRefs = new HashMap<>();

  public static LLVMValueRef getRef(CompileContext ctx, String refName) {
    if (!registerRefs.containsKey(refName)) {
      registerRefs.put(refName, LLVMAddGlobal(ctx.moduleRef, LLVMInt32Type(), refName));
    }
    return registerRefs.get(refName);
  }
}
