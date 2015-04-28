var FOOD = [ {name:'Greens', score:50, energy:30, fat:1, muscle:0, img_id:30, position:{x:0,y:0}, velocity:{x:0,y:200}, gravity:{x:0,y:0}},
             {name:'Reds', score:100, energy:50, fat:5, muscle:4, img_id:58, position:{x:0,y:0}, velocity:{x:0,y:250}, gravity:{x:0,y:0}},
             {name:'Cake', score:250, energy:10, fat:8, muscle:-1, img_id:60, position:{x:0,y:0}, velocity:{x:0,y:250}, gravity:{x:0,y:50}},
             {name:'Spice', score:50, energy:0, fat:-3, muscle:0, img_id:11, position:{x:0,y:500}, velocity:{x:0,y:-1000}, gravity:{x:0,y:1000}},
             {name:'Mushroom', score:50, energy:0, fat:0, muscle:0, img_id:84, position:{x:0,y:0}, velocity:{x:0,y:300}, gravity:{x:0,y:200}},
             {name:'Battery', score:50, energy:5, fat:-1, muscle:-1, img_id:12, position:{x:0,y:0}, velocity:{x:200,y:250}, gravity:{x:0,y:0}},
             {name:'Poison', score:0, energy:-1000, fat:0, muscle:0, img_id:0, position:{x:0,y:0}, velocity:{x:0,y:150}, gravity:{x:0,y:100}},
             {name:'Magnet', score:50, energy:0, fat:0, muscle:0, img_id:13, position:{x:0,y:0}, velocity:{x:200,y:0}, gravity:{x:0,y:400}},
             {name:'DbScore', score:50, energy:0, fat:0, muscle:0, img_id:3, position:{x:0,y:530}, velocity:{x:0,y:0}, gravity:{x:150,y:0}},
             {name:'Emetics', score:-20, energy:-5, fat:-0.5, muscle:-1.5, img_id:17, position:{x:0,y:0}, velocity:{x:100,y:400}, gravity:{x:0,y:400}},
			 {}];
var foodImgKey  = 'foods';
var highScoreRecord = [0,0,0,0,0,0,0,0,0,0];
var PLAYER = {img:'boy' ,initSpeed:500, fullEnergy:1000, initPos:{x:0,y:560}};