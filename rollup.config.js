import nodeResolve from '@rollup/plugin-node-resolve';

export default {
  input: 'dist/esm/index.js',
  output: {
    file: 'dist/plugin.js',
    format: 'iife',
    name: 'firebaseMLVisionExports',
    sourcemap: true
  },
  plugins: [
    nodeResolve()
  ]
};
