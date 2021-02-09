var path = require('path');

module.exports = {
    entry: './src/main/js/App.js',
    devtool: 'inline-source-map',
    cache: true,
    mode: 'development',
    output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js',
        publicPath: '/'
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/env', '@babel/react']
                    }
                }]
            }
        ]
    },
    devServer: {
        historyApiFallback: true,
    },
};