module.exports = {
    devtool: 'source-map',
    module: {
        rules: [{
            test: /\.(js|jsx)$/,
            exclude: /node_modules/,
            loader: "babel-loader",
            options: {
                presets: ['@babel/preset-env', '@babel/preset-react'],
                plugins: [
                    [
                        "@babel/plugin-proposal-class-properties" //for the class propertise syntax
                    ]
                ]
            }
        }]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    }
};