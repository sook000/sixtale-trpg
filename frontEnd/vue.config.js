const webpack = require("webpack");

module.exports = {
  configureWebpack: {
    resolve: {
      alias: {
        vue: "vue/dist/vue.runtime.esm-bundler.js",
      },
    },
    plugins: [
      new webpack.DefinePlugin({
        __VUE_OPTIONS_API__: true,
        __VUE_PROD_DEVTOOLS__: false,
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false,
        "process.env": {
          VUE_APP_DALLE3_API_KEY: JSON.stringify(process.env.VUE_APP_DALLE3_API_KEY),
        },
      }),
    ],
    optimization: {
      minimize: false,
    },
  },
  chainWebpack: (config) => {
    config.plugin("html").tap((args) => {
      args[0].title = "SixTale";
      return args;
    });
  },
  devServer: {
    https: false,
    port: 8083,
    open: true,
    proxy: {
      "/api/v1": {
        target: "https://i11d108.p.ssafy.io", // 백엔드 서버 주소로 변경
        changeOrigin: true,
      },
      // "/ws": {
      //   target: "http://localhost:8888/api/v1", // WebSocket 서버 주소
      //   ws: true, // WebSocket 프록시 활성화
      //   changeOrigin: true,
      // },
    },
    historyApiFallback: true,
    hot: true,
  },
  css: {
    requireModuleExtension: false,
    loaderOptions: {
      postcss: {
        plugins: [
          require("cssnano")({
            preset: [
              "default",
              {
                discardComments: {
                  removeAll: true,
                },
                normalizeWhitespace: false,
                mergeRules: false,
              },
            ],
          }),
        ],
      },
    },
  },
  lintOnSave: false,
  outputDir: "dist",
};
