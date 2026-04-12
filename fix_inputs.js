const fs = require('fs');
const path = require('path');

const replacementCss = `.input-style { 
          background: #171717; 
          border: 1px solid #262626; 
          border-radius: 0.75rem; 
          padding: 0.75rem 1rem; 
          width: 100%; 
          font-size: 0.85rem; 
          color: #fafafa;
          color-scheme: dark;
          transition: all 0.2s ease-in-out;
      }
      .input-style:focus { outline: none; border-color: rgba(107, 255, 143, 0.4); box-shadow: 0 0 0 1px rgba(107, 255, 143, 0.2); }
      textarea.input-style { resize: vertical; min-height: 80px; }
      select.input-style { 
          appearance: none; 
          background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="white" viewBox="0 0 24 24"><path d="M7 10l5 5 5-5z"/></svg>'); 
          background-repeat: no-repeat; 
          background-position: right 1rem center; 
      }`;

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // Simple replace of .input-style {...} down to the potential focus block
            const regex = /\.input-style\s*\{[\s\S]*?\}\s*(?:\.input-style:focus\s*\{[\s\S]*?\})?/g;
            
            if (regex.test(content) && !content.includes('.input-style { \n          background: #171717;')) {
                // If it exists but is not exactly the new one, let's replace it
                content = content.replace(regex, replacementCss);
                modified = true;
            }

            if (modified) {
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Modified CSS:', fullPath);
            }
        }
    }
}

processDirectory(path.join(__dirname, 'app/src/main/assets/stitch'));
console.log('Standardized all inputs globally.');
