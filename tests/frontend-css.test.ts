import { readFileSync } from "node:fs";
import { join } from "node:path";
import { describe, expect, it } from "vitest";

describe("frontend responsive cockpit CSS", () => {
  it("keeps the farm column before the lesson/editor stack on mobile", () => {
    const css = readFileSync(join(process.cwd(), "app/globals.css"), "utf8");

    expect(css).toMatch(/@media\s*\(max-width:\s*900px\)[\s\S]*\.col-right\s*\{[\s\S]*order:\s*1/);
    expect(css).toMatch(/@media\s*\(max-width:\s*900px\)[\s\S]*\.col-left\s*\{[\s\S]*order:\s*2/);
  });
});
