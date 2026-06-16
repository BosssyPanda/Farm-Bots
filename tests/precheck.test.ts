import { describe, expect, it } from "vitest";
import { precheckStrategyCode } from "@/lib/runner/precheck";

describe("precheckStrategyCode", () => {
  it("accepts a beginner Strategy using loops, arrays, helpers, and watch", () => {
    const result = precheckStrategyCode(`public class Strategy {
      public void run(Drone drone, Farm farm) {
        int[] prices = farm.prices();
        for (int i = 0; i < prices.length; i++) {
          drone.watch("i", i);
        }
      }
    }`);

    expect(result.ok).toBe(true);
  });

  it("rejects imports, collections, library sorting, lambdas, threads, and System.exit", () => {
    const samples = [
      "import java.util.ArrayList; public class Strategy { public void run(Drone d, Farm f) {} }",
      "public class Strategy { public void run(Drone d, Farm f) { java.util.Arrays.sort(f.prices()); } }",
      "public class Strategy { public void run(Drone d, Farm f) { Runnable r = () -> {}; } }",
      "public class Strategy { public void run(Drone d, Farm f) { new Thread(); } }",
      "public class Strategy { public void run(Drone d, Farm f) { System.exit(0); } }",
    ];

    for (const sample of samples) {
      const result = precheckStrategyCode(sample);
      expect(result.ok).toBe(false);
      expect(result.message).toMatch(/not allowed|exactly one public class Strategy/);
    }
  });

  it("requires exactly one public Strategy class and no package declaration", () => {
    expect(precheckStrategyCode("package x; public class Strategy {}").ok).toBe(false);
    expect(precheckStrategyCode("public class Other { }").ok).toBe(false);
    expect(precheckStrategyCode("public class Strategy { } public class Strategy { }").ok).toBe(false);
  });
});
