package io.javalanche.covfefe.instructions;

import io.javalanche.covfefe.CompileContext;
import java.util.Map;

@FunctionalInterface
public interface InstructionEmitter {

  Map<String, InstructionEmitter> emitterMap = Map.ofEntries(
      Map.entry("nop", NopInstruction::nop));

  void sink(CompileContext ctx, String[] instruction);

}
