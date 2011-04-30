
describe("Task",function(){
    var task;

   beforeEach(function() {
     task = new Task;
     $.ajax=function(params){}
  });

    it("When saving a task it must fail if doesn't have a title or a Story",function(){
        var errorChecked=false;
        task.bind("error", function(model, error) {
            alert("entra en el error");
            expect(error).toBeDefined();
            errorChecked=true;
        });
        task.save(task.validateAttributes());
        expect(errorChecked).toBeTruthy();
    });


});

