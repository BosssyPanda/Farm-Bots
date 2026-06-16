export interface PrecheckResult {
  ok: boolean;
  message: string;
}

const forbiddenRules: Array<[RegExp, string]> = [
  [/\bimport\b/, "Imports are not allowed. The engine already provides the beginner API."],
  [/\bpackage\b/, "Package declarations are not allowed. Use exactly one public class Strategy."],
  [/\b(ArrayList|LinkedList|HashMap|HashSet|TreeMap|TreeSet|Map|Set|Collection|Collections)\b/, "Collections are not allowed yet. Use arrays for these objectives."],
  [/\bArrays\s*\.\s*(sort|binarySearch)\b/, "Library sorting/searching is not allowed. Write the search or sort by hand."],
  [/\bCollections\s*\./, "Collections helpers are not allowed. Write the algorithm by hand."],
  [/\bjava\s*\.\s*util\s*\.\s*Random\b/, "java.util.Random is not allowed."],
  [/\b(stream|parallelStream)\s*\(/, "Streams are not allowed in beginner Strategy code."],
  [/->/, "Lambdas are not allowed in beginner Strategy code."],
  [/::/, "Method references are not allowed in beginner Strategy code."],
  [/\b(Thread|Runnable|synchronized|volatile)\b/, "Threading and concurrency are not allowed."],
  [/\bSystem\s*\.\s*exit\s*\(/, "System.exit is not allowed."],
  [/\b(java\s*\.\s*io|File|Files|Path|Paths|Socket|URL|URI|ClassLoader|Class\s*\.)\b/, "File, network, and reflection APIs are not allowed."],
  [/\bRuntime\s*\.\s*getRuntime\b|\bProcessBuilder\b/, "Launching processes is not allowed."],
];

export function precheckStrategyCode(code: string): PrecheckResult {
  const trimmed = code.trim();
  if (!trimmed) {
    return fail("Strategy.java is empty. Write a public class Strategy with run(Drone drone, Farm farm).");
  }

  const publicStrategyMatches = trimmed.match(/\bpublic\s+class\s+Strategy\b/g) ?? [];
  if (publicStrategyMatches.length !== 1) {
    return fail("Use exactly one public class Strategy.");
  }

  const publicClassMatches = trimmed.match(/\bpublic\s+class\s+[A-Za-z_][A-Za-z0-9_]*\b/g) ?? [];
  if (publicClassMatches.length !== 1) {
    return fail("Use exactly one public class Strategy. Helper methods go inside that class.");
  }

  if (!/\bvoid\s+run\s*\(\s*Drone\s+[A-Za-z_][A-Za-z0-9_]*\s*,\s*Farm\s+[A-Za-z_][A-Za-z0-9_]*\s*\)/.test(trimmed)) {
    return fail("Strategy must include public void run(Drone drone, Farm farm).");
  }

  const searchable = stripCommentsAndStrings(trimmed);
  for (const [pattern, message] of forbiddenRules) {
    if (pattern.test(searchable)) {
      return fail(message);
    }
  }

  return { ok: true, message: "" };
}

function fail(message: string): PrecheckResult {
  return { ok: false, message };
}

function stripCommentsAndStrings(code: string): string {
  let output = "";
  let i = 0;
  let mode: "code" | "line" | "block" | "string" | "char" = "code";

  while (i < code.length) {
    const c = code[i];
    const n = code[i + 1];

    if (mode === "line") {
      if (c === "\n") {
        mode = "code";
        output += "\n";
      }
      i++;
      continue;
    }

    if (mode === "block") {
      if (c === "*" && n === "/") {
        mode = "code";
        i += 2;
      } else {
        i++;
      }
      continue;
    }

    if (mode === "string") {
      if (c === "\\") {
        i += 2;
      } else if (c === "\"") {
        mode = "code";
        output += "\"\"";
        i++;
      } else {
        i++;
      }
      continue;
    }

    if (mode === "char") {
      if (c === "\\") {
        i += 2;
      } else if (c === "'") {
        mode = "code";
        output += "''";
        i++;
      } else {
        i++;
      }
      continue;
    }

    if (c === "/" && n === "/") {
      mode = "line";
      i += 2;
      continue;
    }
    if (c === "/" && n === "*") {
      mode = "block";
      i += 2;
      continue;
    }
    if (c === "\"") {
      mode = "string";
      i++;
      continue;
    }
    if (c === "'") {
      mode = "char";
      i++;
      continue;
    }

    output += c;
    i++;
  }

  return output;
}
