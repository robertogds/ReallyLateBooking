/**
 * Created by .
 * User: robertogds
 * Date: 26-Feb-2011
 * Time: 17:13:41
 */
describe("Story",function(){
    var story;

   beforeEach(function() {
    story = new Story();
  });

    it("Should have an operation to add a new task",function(){
        var task = new Task;
        story.addTask(task);
        var numberOfTasks = story.get("tasks").length;
        expect(numberOfTasks).toEqual(1);
        expect(task.story.id).toEqual(story.id);
    });


    it("Saving a Story fails if not name provided",function(){
        var errorChecked=false;
        story.bind("error", function(model, error) {
            expect(error).toBeDefined();
            errorChecked=true;
        });
        story.save();
        expect(errorChecked).toBeTruthy();
    });

    it("I can set a title and then a description and it should work when done without validation",function(){
        story.bind("error", function(model, error) {
            expect(false).toBeTruthy();
        });
        story.set({title:"title of story"},{silent: true});
        story.set({description:"description of story"},{silent: true});
        story.change();
    });

    it("When I save a Story it should call the right endpoint with the JSON data including the tasks",function(){
        story.bind("error", function(model, error) {
            expect(false).toBeTruthy();
        });
        story.set({title:"title of story"},{silent: true});
        story.set({description:"description of story"},{silent: true});
        var task = new Task({title:"titulotarea"});
        story.addTask(task);
        story.change();
        $.ajax=function(params){
            expect(params.data).toContain("titulotarea");
        }
        story.save();
    });
});

