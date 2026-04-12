const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

const mockupsDir = path.join(__dirname, 'Actual_UI_Mockups');
const screenshotsDir = path.join(__dirname, 'Actual_UI_Mockups', 'Screenshots');

if (!fs.existsSync(screenshotsDir)) {
    fs.mkdirSync(screenshotsDir);
}

(async () => {
    console.log("Launching browser...");
    const browser = await puppeteer.launch({ headless: 'new' });
    const page = await browser.newPage();
    
    // Set viewport to exact mobile size
    await page.setViewport({ width: 375, height: 812, deviceScaleFactor: 2 });

    const files = fs.readdirSync(mockupsDir);
    const htmlFiles = files.filter(f => f.endsWith('.html') && f !== 'index.html');

    for (const file of htmlFiles) {
        const filePath = `file:///${path.join(mockupsDir, file).replace(/\\/g, '/')}`;
        console.log(`Processing: ${file}...`);
        
        // Wait until network is mostly idle to ensure Tailwind CSS and fonts loaded
        await page.goto(filePath, { waitUntil: 'networkidle0', timeout: 30000 }).catch(e => console.log("Timeout, but capturing anyway: " + e.message));
        
        const outputName = file.replace('.html', '.png');
        const outputPath = path.join(screenshotsDir, outputName);
        
        await page.screenshot({ path: outputPath });
        console.log(`Saved screenshot: ${outputPath}`);
    }

    await browser.close();
    console.log("All screenshots captured successfully at:");
    console.log(screenshotsDir);
})();
