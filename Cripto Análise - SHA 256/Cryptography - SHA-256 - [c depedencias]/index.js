
const fileBytes = require('file-bytes');
const crypto = require('crypto');
const path = require('path');
const fs = require('fs');

const fileHash = '8e423302209494d266a7ab7e1a58ca8502c9bfdaa31dfba70aa8805d20c087bd';
const fileCheckName = 'video_03.mp4';
//const fileCheckName = 'video05.mp4';


const main = async () => {

  console.log('********************************************************\n');
  console.log('Cryptography - SHA-256');
  console.log('Terceiro trabalho da disciplina de Seguranca de Sistemas');
  console.log('Autor: David Vieira\n');
  console.log('********************************************************\n\n');

  const blockSize = 1024; // 1kb

  const filePath = path.join(__dirname + '/Video/' + fileCheckName);
  const hashCheck = fileHash;

  const h0 = await computeHash(filePath, blockSize);
  const h0Hex = h0.toString('hex');
  console.log(`\n H0 calculated for file ${fileCheckName}: ${h0Hex}`);
};

const computeHash = async (filePath, blockSize) => {
  try {
    const fileSize = await fileBytes(filePath);
    const lastBlockSize = fileSize % blockSize;

    console.log(`Opening file: ${filePath}  ;  ${fileSize} bytes;`);
    const fp = await fs.openSync(filePath, 'r');
    const buffer = await processChunks(fp, fileSize, lastBlockSize, blockSize);

    fs.close(fp, (err) => {
      if (err) throw err;
    });

    return buffer;
  } catch(err) {
    console.log(err);
  }
};

const processChunks = async (fp, fileSize, lastBlockSize, blockSize) => {
  let i = 0, lastPosition = fileSize, resultHash = '';

  while(lastPosition > 0) {
    let size = blockSize;
    if(i === 0) size = lastBlockSize;

    console.log(`Reading file between ${lastPosition} and ${lastPosition - size}`);
    
    let buffer = Buffer.alloc(size);
    fs.readSync(fp, buffer, 0, buffer.length, lastPosition - size);
    const hash = crypto.createHash('sha256');
    hash.update(buffer);
    
    if (resultHash) {
      hash.update(resultHash);
    }
    resultHash = hash.digest();
    lastPosition -= size;
    i++;
  }
  return resultHash;
};

main();