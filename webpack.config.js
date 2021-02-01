var path = require('path');

module.exports = {
    entry: './src/main/js/App.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js',
        publicPath: '/'
    },
    module: {
            rules: [
                { test: /\.txt$/, use: 'raw-loader' }
            ]
    //     rules: [
    //         {
    //             test: path.join(__dirname, '.'),
    //             exclude: /(node_modules)/,
    //             use: [{
    //                 loader: 'babel-loader',
    //                 options: {
    //                     presets: ["@babel/preset-env", "@babel/preset-react"]
    //                 }
    //             }]
    //         }
    //     ]
    },
    devServer: {
        historyApiFallback: true,
    },
};