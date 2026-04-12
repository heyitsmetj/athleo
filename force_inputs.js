const fs = require('fs');
const path = require('path');

const globalCss = `
<!-- FORCED GLOBAL STANDARDIZATION -->
<style>
input:not([type="file"]), textarea, select { 
    background-color: #171717 !important; 
    border: 1px solid #262626 !important; 
    color: #fafafa !important; 
    border-radius: 0.75rem !important;
    padding: 0.85rem 1.25rem !important;
    color-scheme: dark !important;
    font-size: 0.9rem !important;
}
input:focus, textarea:focus, select:focus { 
    border-color: rgba(107, 255, 143, 0.6) !important; 
    outline: none !important; 
    box-shadow: 0 0 0 1px rgba(107, 255, 143, 0.2) !important; 
}
select {
    appearance: none !important; 
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="white" viewBox="0 0 24 24"><path d="M7 10l5 5 5-5z"/></svg>') !important; 
    background-repeat: no-repeat !important; 
    background-position: right 1.25rem center !important; 
}
</style>
</head>`;

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            if (!content.includes('FORCED GLOBAL STANDARDIZATION')) {
                // Remove the old buggy specific overrides first to be safe
                content = content.replace(/input,\s*textarea,\s*select\s*\{[^}]+\}/g, '');
                content = content.replace(/input:focus,\s*textarea:focus,\s*select:focus\s*\{[^}]+\}/g, '');
                
                content = content.replace('</head>', globalCss);
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Forced CSS:', fullPath);
            }
        }
    }
}

processDirectory(path.join(__dirname, 'app/src/main/assets/stitch'));
