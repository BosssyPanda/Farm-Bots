// three ships its addons (examples/jsm) as JS without bundled .d.ts under
// moduleResolution "bundler". Declare them ambiently so the island scene can
// import GLTFLoader / OrbitControls / the postprocessing passes. Loose typing is
// fine here — the scene is self-contained and verified at runtime.
declare module "three/examples/jsm/*";
