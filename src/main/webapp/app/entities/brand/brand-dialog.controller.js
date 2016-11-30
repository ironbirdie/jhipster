(function() {
    'use strict';

    angular
        .module('jhipsterstoreApp')
        .controller('BrandDialogController', BrandDialogController);

    BrandDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Brand', 'Product'];

    function BrandDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Brand, Product) {
        var vm = this;

        vm.brand = entity;
        vm.clear = clear;
        vm.save = save;
        vm.products = Product.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.brand.id !== null) {
                Brand.update(vm.brand, onSaveSuccess, onSaveError);
            } else {
                Brand.save(vm.brand, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jhipsterstoreApp:brandUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
