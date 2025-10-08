#!/usr/bin/env node
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const os = require('os');

const green = (s) => `\x1b[32m${s}\x1b[0m`;
const yellow = (s) => `\x1b[33m${s}\x1b[0m`;
const red = (s) => `\x1b[31m${s}\x1b[0m`;

function log(msg) { console.log(msg); }

function hasModule(mod) {
  try {
    require.resolve(mod);
    return true;
  } catch {
    return false;
  }
}

function install(pkg, dev = true) {
  const flag = dev ? '--save-dev' : '--save';
  log(`📦 Installing ${pkg} ...`);
  execSync(`npm install ${flag} ${pkg} --no-audit --no-fund`, { stdio: 'inherit' });
}

function ensurePkgJson() {
  const pkgPath = path.join(process.cwd(), 'package.json');
  if (!fs.existsSync(pkgPath)) {
    console.error(red('❌ package.json not found in current directory'));
    process.exit(1);
  }
}

function checkNode() {
  const version = process.versions.node;
  const [major] = version.split('.').map(Number);
  if (major < 18 || major >= 26) {
    log(yellow(`⚠️ Node.js ${version} detected; recommended range is >=18 and <26.`));
    log(yellow('   Suggested: nvm install 22 && nvm use 22'));
  } else {
    log(green(`✅ Node.js version ${version} is compatible`));
  }
  try {
    const npmVersion = execSync('npm --version').toString().trim();
    log(green(`✅ npm version ${npmVersion}`));
  } catch {
    log(red('❌ npm not found'));
    process.exit(1);
  }
}

function maybeRepairLock() {
  const lockPath = path.join(process.cwd(), 'package-lock.json');
  if (fs.existsSync(lockPath)) {
    try {
      JSON.parse(fs.readFileSync(lockPath, 'utf-8'));
    } catch {
      log(yellow('⚠️ package-lock.json appears corrupted. Recreating...'));
      fs.rmSync('node_modules', { recursive: true, force: true });
      fs.rmSync('package-lock.json', { force: true });
      execSync('npm cache clean --force', { stdio: 'inherit' });
      execSync('npm install', { stdio: 'inherit' });
    }
  }
}

function ensureDependencies() {
  const required = [
    'react-scripts',
    'workbox-webpack-plugin',
    'jest-worker',
    'webpack',
    'babel-loader'
  ];
  required.forEach((pkg) => {
    if (!hasModule(pkg)) {
      log(yellow(`⚠️ ${pkg} missing → installing...`));
      const dev = pkg !== 'webpack' ? true : true;
      install(`${pkg}` , dev);
    } else {
      log(green(`✅ ${pkg} found`));
    }
  });

  // CRA updates and dedupe
  try { execSync('npx update-browserslist-db@latest', { stdio: 'inherit' }); } catch {}
  try { execSync('npm dedupe', { stdio: 'inherit' }); } catch {}
}

function main() {
  ensurePkgJson();
  checkNode();
  maybeRepairLock();
  ensureDependencies();
}

main();


