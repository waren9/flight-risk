/*
  Ensure CRA dependencies can resolve jest-worker on newer Node versions by
  creating a minimal shim if the package is not installed with build output.
*/
const fs = require('fs');
const path = require('path');

const pkgPath = path.join(__dirname, '..', 'node_modules', 'jest-worker');
const buildIndex = path.join(pkgPath, 'build', 'index.js');

try {
  if (!fs.existsSync(pkgPath)) {
    // nothing to do, user may not need it
    process.exit(0);
  }
  const buildDir = path.dirname(buildIndex);
  if (!fs.existsSync(buildIndex)) {
    fs.mkdirSync(buildDir, { recursive: true });
    // Minimal shim exports used by CRA tooling
    const shim = `
      class Worker {}
      module.exports = { Worker, default: { Worker } };
    `;
    fs.writeFileSync(buildIndex, shim);
  }
} catch (_) {
  // best-effort only
}


