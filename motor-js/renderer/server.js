const prerender = require('prerender');
const server = prerender({
    chromeLocation: '/usr/bin/chromium-browser',
    extraChromeFlags:['--no-sandbox']
});
server.start();
