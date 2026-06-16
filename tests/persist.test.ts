import { describe, expect, it, beforeEach } from "vitest";
import {
  clearAllStrategyCode,
  clearFarmState,
  clearStrategyCode,
  createDefaultFarmState,
  loadFarmState,
  loadStrategyCode,
  saveFarmState,
  saveStrategyCode,
} from "@/lib/persist";

describe("persist helpers", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("returns a full farm-state fallback when nothing is stored", () => {
    const state = loadFarmState("first-sprout");

    expect(state).toEqual(createDefaultFarmState("first-sprout"));
    expect(state.tiles).toEqual([]);
    expect(state.resources).toEqual({});
    expect(state.concepts).toEqual({});
  });

  it("stores strategy code separately for each objective", () => {
    saveStrategyCode("first-sprout", "code-one");
    saveStrategyCode("the-long-rows", "code-two");

    expect(loadStrategyCode("first-sprout", "starter")).toBe("code-one");
    expect(loadStrategyCode("the-long-rows", "starter")).toBe("code-two");
    expect(loadStrategyCode("missing", "starter")).toBe("starter");
  });

  it("persists the farm state round trip", () => {
    const state = createDefaultFarmState("the-long-rows");
    state.width = 8;
    state.tick = 42;
    state.unlocked = ["bigger-field"];

    saveFarmState(state);

    expect(loadFarmState("first-sprout")).toEqual(state);
  });

  it("clears farm progress without touching saved code", () => {
    const state = createDefaultFarmState("the-long-rows");
    saveFarmState(state);
    saveStrategyCode("the-long-rows", "saved-code");

    clearFarmState();

    expect(loadFarmState("first-sprout")).toEqual(createDefaultFarmState("first-sprout"));
    expect(loadStrategyCode("the-long-rows", "starter")).toBe("saved-code");
  });

  it("clears current or all saved strategy code", () => {
    saveStrategyCode("first-sprout", "code-one");
    saveStrategyCode("the-long-rows", "code-two");

    clearStrategyCode("first-sprout");

    expect(loadStrategyCode("first-sprout", "starter")).toBe("starter");
    expect(loadStrategyCode("the-long-rows", "starter")).toBe("code-two");

    clearAllStrategyCode();

    expect(loadStrategyCode("the-long-rows", "starter")).toBe("starter");
  });
});
