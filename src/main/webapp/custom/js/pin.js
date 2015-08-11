(function($, window, document){

    "use strict"

    var PIN_URL_ROOT = '/iPersonal/dashboard/pins';

    var Pin = Base.extend({
    	urlRoot: PIN_URL_ROOT,
        
        initialize: function() {
            this.mandatory = {
                'name': true, 'imageUrl': true, 'description': true
            },
            this.maxLength = {
                'name': 50, 'imageUrl': 150, 'description': 1000
            },
            this.formAttributes = ['name', 'imageUrl', 'description']
        }

    }); 

    var Pins = Backbone.Collection.extend({
        model: Pin
    })

    var PinView = BaseView.extend({

        el: $('#pin-wrapper'),
        entityType: 'PIN',
        createTemplate: $('#pin-create-template').html(),
        displayTemplate: $('#pin-display-template').html(),

        events : {
            'click #pin-submit': 'createPin',
            'click #pin-cancel': 'resetValues',
            'click #pin-tag-img': 'displayTagSelection',
            'click img.delete': 'deleteEntity'
        },

        prepareVariables: function() {
            this.tagImage = $('#pin-tag-img');
            this.searchTag =  $('#pin-tag');
            this.saveForm =  $('#pin-form');

        },

        createPin: function(e) {

            var self = this;
            e.preventDefault();

            self.model = new Pin();
            self.model.set({
                name: self.saveForm.find('[name=name]').val(),
                imageUrl: self.saveForm.find('[name=imageUrl]').val(),
                description: self.saveForm.find('[name=description]').val()
            });

            self.model.on('invalid', function(model , error) {
                self.renderErrors(error);
            });

            var result = self.model.save({
                success: function(response) {},
                error: function(error) {}
            });

            if (result) {
                result.complete(function(response){
                    if (response.status != 201) {
                        var errors = self.buildErrorObject(response, self);
                        self.renderErrors(errors);
                    } else {
                        var pinId = response.responseText;
                        var tags = self.searchTag.val();
                        self.postCreation(pinId, "PIN", self.model.get('name'), 1, tags)
                        self.model.set({
                            pinId: pinId,
                            'createdOn': Math.floor(Date.now()),
                            'modifiedAt': Math.floor(Date.now()),
                            'tags': tags
                        });
                        self.collection.unshift(self.model);
                        var entityList = self.buildEntityList();
                        backboneGlobalObj.trigger('entity:displaylist', entityList);
                    }
                });
            }
        },


        fetchPins: function(e) {

            var self = this;

            if (e) {
                e.preventDefault();
            }

            if (this.collection.length  == parseInt(entityCountModel.attributes.pins)) {
                var entityList = this.buildEntityList();
                backboneGlobalObj.trigger('entity:displaylist', entityList);
                return;
            }
             
            this.model = new Pin();   
            this.model.fetch({data: {offset: this.collection.length, limit : 20} }).complete(function(response){
                if (response.status == 200) {
                    var pins = JSON.parse(response.responseText)['pin'];
                    if (pins instanceof Array) {
                        for (var index in pins) {
                            var pin = new Pin(pins[index])
                            self.collection.push(pin);
                        }
                    } else if (pins) {
                        var pin = new Pin(pins);
                        self.collection.push(pin);
                    }
                    var entityList = self.buildEntityList();
                    backboneGlobalObj.trigger('entity:displaylist', entityList);
                }
            });
        },

        buildEntityList: function() {

            var entityList = [];

            for (var i = 0; i < this.collection.length; i++) {
                var description = this.collection.models[i].attributes.description;
                var entity = {
                    'entityId' : this.collection.models[i].attributes.pinId,
                    'entityTitle' : this.collection.models[i].attributes.name,
                    'url': this.collection.models[i].attributes.imageUrl,
                    'entitySummary': description ? description.substring(0,100) : description,
                    'entityType': 'pin',
                    'modifiedAt': this.collection.models[i].attributes.modifiedAt,
                };
                entityList.push(entity);
            }
            return entityList;
        },

        getDeletableModel: function(pinId) {

            return new Pin({
                id: pinId
            });
        },

        findIndex: function(pinId) {
            for (var i = 0; i < this.collection.models.length; i++) {
                if (this.collection.models[i].attributes.pinId == pinId) {
                    break;
                }
            }
            return i;
        }
    });

    window.pinView = new PinView({ collection: new Pins() });

})(jQuery, window, document);