const path = require('path');

module.exports = {
    paths: function (paths, env) {        
        paths["appIndexJs"] = path.resolve(__dirname, 'src/main/react/index.tsx');
        paths["appSrc"]     = path.resolve(__dirname, 'src/main/react');
        paths["appPublic"]  = path.resolve(__dirname, 'src/main/react-public');
        paths["appHtml"]    = path.resolve(__dirname, 'src/main/react-public/index.html');
        paths["appBuild"]   = path.resolve(__dirname, 'src/main/webapp');

        return paths;
    },
}