const String = require('node-sass').types.String;

module.exports = {
    ["THEME()"](){
        return new String(process.env.THEME || "dark");
    }
}
