import { readdir, readFile } from "node:fs/promises";
import path from "node:path";

const ENGINE_DIR = path.join(process.cwd(), "engine");

export interface EngineFile {
  path: string;
  content: Buffer;
}

export async function readEngineFiles(strategyCode: string): Promise<EngineFile[]> {
  const names = await readdir(ENGINE_DIR);
  const files: EngineFile[] = [];
  for (const name of names.sort()) {
    if (!isCanonicalJavaSource(name) || name === "Strategy.java") continue;
    files.push({
      path: name,
      content: await readFile(path.join(ENGINE_DIR, name)),
    });
  }
  files.push({ path: "Strategy.java", content: Buffer.from(strategyCode, "utf8") });
  return files;
}

export async function readSampleStrategy(): Promise<string> {
  return readFile(path.join(ENGINE_DIR, "Strategy.java"), "utf8");
}

function isCanonicalJavaSource(name: string): boolean {
  return /^[A-Za-z_][A-Za-z0-9_]*\.java$/.test(name);
}
