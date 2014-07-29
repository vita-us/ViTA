'use strict';

module.exports = function(grunt) {

  var appPath = '../main/webapp/', javascriptPath = appPath + 'js/', cssPath = appPath + 'css/';

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    bowerrc: grunt.file.readJSON('.bowerrc'),
    concat: {
      options: {
        // Append a short comment of the path for each concatenated source file
        process: function(src, filepath) {
          return '// Source: ' + filepath + '\n' + src;
        }
      },
      graph: {
        src: ['app/js/**/*.js'],
        dest: javascriptPath + '<%= pkg.name %>.js'
      }
    },
    copy: {
      dependencies: {
        files: [{
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular/',
          src: ['angular.js'],
          dest: javascriptPath
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-route/',
          src: ['angular-route.js'],
          dest: javascriptPath
        }]
      },
      statics: {
        files: [{
          expand: true,
          cwd: 'app/css/',
          src: ['**'],
          dest: cssPath
        }, {
          expand: true,
          cwd: 'app/',
          src: ['index.html'],
          dest: appPath
        }, {
          expand: true,
          cwd: 'app/partials',
          src: ['**'],
          dest: appPath + 'partials/'
        }]
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-copy');

  grunt.registerTask('build', ['copy', 'concat'])
  grunt.registerTask('default', ['build']);
};