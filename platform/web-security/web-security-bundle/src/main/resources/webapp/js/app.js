(function () {
    'use strict';

    /* App Module */

    angular.module('motech-web-security', ['motech-dashboard', 'roleService', 'userService', 'permissionService', 'ngCookies', 'bootstrap']).config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('websecurity', {abstract: true, templateUrl: '../websecurity/index.html'})
                .state('websecurity.users', {url: '/websecurity/users', templateUrl: '../websecurity/partials/user.html', controller: 'UserCtrl'})
                .state('websecurity.roles', {url: '/websecurity/roles', templateUrl: '../websecurity/partials/role.html', controller: 'RoleCtrl'})
                .state('websecurity.profile', {url: '/websecurity/profile/:username', templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'});
    }]).filter('filterPagination', function() {
        return function(input, start) {
            start= +start;
            return input.slice(start);
        };}).
    filter('repeat', function(){return function(input, total) {var i; total = parseInt(total, 10);for (i=0; i<total; i+=1) { input.push(i);} return input;};}).
    directive('roleNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
               ctrl.$parsers.unshift(function(viewValue) {
                        var names = [], i;
                        for (i=0; i<scope.roleList.length; i+=1) {
                            names.push(scope.roleList[i].roleName);
                        }
                        if(scope.addOrEdit === "edit"){
                            names.removeObject(scope.role.originalRoleName);
                        }
                        if(names.indexOf(viewValue)===-1) {
                            scope.pwdNameValidate=true;
                            ctrl.$setValidity('pwd', true);
                            return viewValue;
                        } else {
                            scope.pwdNameValidate=false;
                            ctrl.$setValidity('pwd', false);
                            return viewValue;
                        }
               });
            }
        };
    }).directive('userNameValidate', function(){
           return {
               require: 'ngModel',
               link: function(scope, elm, attrs, ctrl) {
                  ctrl.$parsers.unshift(function(viewValue) {
                           var names = [], i;
                           for (i=0; i<scope.userList.length; i+=1) {
                               names.push(scope.userList[i].userName);
                           }
                           if(names.indexOf(viewValue)===-1) {
                               scope.pwdNameValidate=true;
                               ctrl.$setValidity('pwd', true);
                               return viewValue;
                           } else {
                               scope.pwdNameValidate=false;
                               ctrl.$setValidity('pwd', false);
                               return viewValue;
                           }
                  });
               }
           };
    }).directive('confirmPassword', function(){
         return {
                restrict: 'A',
                require: 'ngModel',
                link: function(scope, elm, attrs, ctrl) {

                    function validateEqual(confirmPassword, userPassword) {
                        if (confirmPassword === userPassword) {
                            ctrl.$setValidity('equal', true);
                            return confirmPassword;
                        } else {
                            ctrl.$setValidity('equal', false);
                            return undefined;
                        }
                    }

                    scope.$watch(attrs.confirmPassword, function(userViewPassword) {
                        validateEqual(ctrl.$viewValue, userViewPassword);
                    });

                    ctrl.$parsers.unshift(function(viewValue) {
                        return validateEqual(viewValue, scope.$eval(attrs.confirmPassword));
                    });

                    ctrl.$formatters.unshift(function(modelPassword) {
                        return validateEqual(modelPassword, scope.$eval(attrs.confirmPassword));
                    });
                }
         };
    });
}());

