var gulp = require('gulp'),
        sassVariables = require('gulp-sass-variables'),
        debug = require('gulp-debug'),
        sass = require('gulp-sass');

var themes = ['dark', 'light', 'green'];

// Compile css
themes.forEach(theme => {
    gulp.task(theme, function () {
        return gulp.src('./src/*.scss')
                .pipe(sassVariables({
                    $theme: theme
                }))
                .pipe(sass())
                .pipe(debug({title: theme}))
                .pipe(gulp.dest('../api/src/main/resources/stylesheet/' + theme))
    });
});

var defaultTasks = [];
themes.forEach(task => {
    defaultTasks.push(task);
});
gulp.task('default', defaultTasks);