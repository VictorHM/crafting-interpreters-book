package lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
  private final Map<String, Object> values = new HashMap();

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    throw new RuntimeError(name,
      "Undefined variable '" + name.lexeme + "'.");
  }
  
  // Inserts a new entry in the map. Meaning new variable definition.
  // TODO ver como modificar esto para impedir redefiniciones de variables.
  // Facil, solo comprobar si existe, y si es asi decidir si cambia el valor o dar error de sintaxis.
  // Aunque interacciona mal con la REPL. Ver consideraciones en pag 120 Crafting interpreters.
  void define(String name, Object value) {
    values.put(name, value);
  }
}

