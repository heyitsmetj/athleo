const fs = require('fs');
const path = require('path');

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            if (content.includes('padding: 0.85rem 1.25rem !important;')) {
                // Only enforce vertical padding so horizontal padding like 'pl-10' for icons stays intact
                content = content.replace('padding: 0.85rem 1.25rem !important;', 'padding-top: 0.85rem !important; padding-bottom: 0.85rem !important;');
                fs.writeFileSync(fullPath, content, 'utf8');
            }
        }
    }
}

processDirectory(path.join(__dirname, 'app/src/main/assets/stitch'));
console.log('Fixed padding globally!');
