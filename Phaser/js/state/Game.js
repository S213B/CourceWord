BasicGame.Game = function (game) {
this.game = game;
this.isGameOver = false;
this.food = [];
};

BasicGame.Game.prototype = {
	
  preload: function() {},
  
  create: function () {
    // Game physics
    this.game.physics.startSystem(Phaser.Physics.ARCADE);
    //this.game.physics.arcade.gravity.y = 250;
    
    // Background
    this.bg = new Background(this.game);  
      
    // Text display
    this.HUD = new HUD(this.game);
      
    // Player
    this.player = new Player(this.game, this.HUD);
    
    // Record
    this.interface = new Interface(this, this.player, this.HUD);
    
    // Food
    this.food.push(new Food(this.game,this.player, FOOD[0]));	//Greens
    FOOD[0].img_id = 92;
    this.food.push(new Food(this.game,this.player, FOOD[0]));	//Greens
    FOOD[0].img_id = 85;
    this.food.push(new Food(this.game,this.player, FOOD[0]));	//Greens
    FOOD[0].img_id = 83;
    this.food.push(new Food(this.game,this.player, FOOD[0]));	//Greens
    FOOD[1].img_id = 53;
    this.food.push(new Food(this.game,this.player, FOOD[1]));	//Reds
    FOOD[1].img_id = 54;
    this.food.push(new Food(this.game,this.player, FOOD[1]));	//Reds
    FOOD[1].img_id = 55;
    this.food.push(new Food(this.game,this.player, FOOD[1]));	//Reds
    this.food.push(new Food(this.game,this.player, FOOD[2]));	//Cake
    this.food.push(new Food(this.game,this.player, FOOD[2]));	//Cake
    this.food.push(new Spice(this.game,this.player, FOOD[3]));	//Spice
	this.food.push(new Mushroom(this.game,this.player, FOOD[4]));	//Mushroom
	this.food.push(new Battery(this.game,this.player, FOOD[5]));	//Battery
	this.food.push(new Magnet(this.game,this.player, FOOD[7], this.food));	//Magnet
	this.food.push(new DbScore(this.game,this.player, FOOD[8]));	//DbScore
	this.food.push(new Emetics(this.game,this.player, FOOD[9]));	//Emetics
	this.food.push(new Poison(this.game,this.player, FOOD[6]));		//Poison
    
	
    for(i = 0;i < this.food.length; i++){
        this.food[i].kill();
        //this.food[i].appear();
    }
	console.log(this.food);
	
      
    // Game control
    this.cursors = this.game.input.keyboard.createCursorKeys();
      
    // Quit button
    this.quitButton = new LabelButton(this.game, 700, 0, 'btn', 'Quit', this.gameOver,this, 1, 0, 2, 0);

	//Pause button
	this.pauseButton = new LabelButton(this.game, 500, 0, 'btn', 'Pause', this.pauseGame,this, 1, 0, 2, 0);

	//Resume button
    this.resumeButton = new LabelButton(this.game, 400, 300, 'btn', 'Resume', this.gameOver,this, 1, 0, 2, 0);
	this.resumeButton.visible = false;

    // Sound effect
    this.fx = this.game.add.audio('sfx');
    //this.fx.addMarker('clickBtn',0,1.0);
    this.fx_food = this.game.add.audio('sfx_food');
    this.fx_food.addMarker('eatFood',0,1.0);
      
    // backgound music
    this.bgm = this.game.add.audio('bgm_game');
    this.bgm.play("",0,0.1,true);
  },

  update: function () {
    // check state
    if(!this.isGameOver){
        this.bg.update();
        this.interface.checkPlayer();
        this.interface.update();
    // player control
    
        if (this.cursors.left.isDown && this.player.body.x > 0){
            if(this.cursors.left.timeDown - this.cursors.left.timeUp < 300 && this.cursors.left.duration < 100){
                this.player.dash('left_dash');               
            }
            else{
                this.player.walk('left');
            }        
        }
        else if (this.cursors.right.isDown && this.player.body.x < 745){
            if(this.cursors.right.timeDown - this.cursors.right.timeUp < 300 && this.cursors.right.duration < 100){
                this.player.dash('right_dash');
            }
            else{
                this.player.walk('right');
            } 
        }
        else {
            this.player.stand();
        }
		
    // Collision
    this.game.physics.arcade.collide(this.player, this.bg.ground); 
    for(i = 0;i < this.food.length; i++){
		if(this.food[i].context.name != 'Emetics' && this.food[i].context.name != 'DbScore')
        	this.game.physics.arcade.collide(this.food[i], this.bg.ground, this.interact);
		else
    		this.game.physics.arcade.collide(this.food[i], this.bg.ground); 
        this.game.physics.arcade.overlap(this.food[i], this.player, this.interact, null, this);
    }  

    // random drop
		var rnd = this.game.world.randomX;
		if(rnd < 400) {
			var temp = this.game.world.randomX;
			if(temp < 200 && this.food[parseInt(rnd/100,10)].alive == false)
				this.food[parseInt(rnd/100,10)].appear();
		} 
        else if(rnd < 600) {
			var temp = this.game.world.randomX;
			if(temp < 400 && this.food[parseInt(rnd/100,10)].alive == false)
				this.food[parseInt(rnd/100,10)].appear();
		} 
        else if(rnd < 700) {
			if(this.food[parseInt(rnd/50,10)-6].alive == false)
				this.food[parseInt(rnd/50,10)-6].appear();
		} 
        else {
			var temp = this.game.world.randomX;
			if(temp < 30) {
				rnd = rnd - 700;
				if(this.food[parseInt(rnd/15,10)+9].alive == false)
					this.food[parseInt(rnd/15,10)+9].appear();
			}
		}
    }
  },
    
  interact: function (food, player) {
    if(player != this.player){
        //food.appear();
		if(food.context.name != 'Emetics')
			food.kill();
    }
    else {
        food.effect();
    }
  },
    
  createBtn: function() {
    this.restartButton = new LabelButton(this.game, 400, 200, 'btn', 'Restart', this.restart,this, 1, 0, 2);
    this.menuButton = new LabelButton(this.game, 400, 400, 'btn', 'Menu', this.quitGame,this, 1, 0, 2);
  },
    
  destroyBtn: function() {
    this.restartButton.destroy();
    this.menuButton.destroy();
  },
    
  gameOver: function () {

    this.quitButton.visible = false;
	this.pauseButton.visible = false;

    this.isGameOver = true;

	//Update record
    this.interface.addHiScore(this.interface.score);
    
    // BGM
    this.fx.play();
    this.bgm.stop();
    
    // kill player and all the food
    this.player.kill();
    for(i = 0;i < this.food.length; i++){
        this.food[i].kill();
    }

    this.game.time.events.removeAll();

    this.createBtn();
        
    // BGM
    this.bgm_end = this.game.add.audio('bgm_end');
    this.bgm_end.play("",0,0.1);
  },


  restart: function () {

    //  A new game starts
    this.quitButton.visible = true;
	this.pauseButton.visible = true;
    
    // BGM
    this.bgm_end.stop();
    this.fx.play();
    this.bgm.play("",0,0.1,true);

    // Food and Player
	/*
    for(i = 0;i < this.food.length; i++){
        this.food[i].appear();
    }
	*/
      
    this.isGameOver = false;

    // Revives the player   
    this.player.revive();
    this.player.restart();

    // Destroy Buttons
    this.destroyBtn();

  },
    
  render: function() {
    //this.food[3].render();
  },

  quitGame: function (pointer) {
    //this.game.time.events.add(Phaser.Timer.SECOND *2, this.quitGame, this);
    //  Here you should destroy anything you no longer need.
    //  Stop music, delete sprites, purge caches, free resources, all that good stuff.
    this.fx.play();
    this.isGameOver = false;
    this.bgm_end.stop();
	this.food = [];

    //  Then let's go back to the main menu.
    this.state.start('MainMenu');
  },

  pauseGame: function () {
  	this.resumeButton.visible = true;
	this.quitButton.visible = false;
	this.pauseButton.visible = false;
	this.game.input.onDown.add(this.resumeGame, this);
  	this.game.paused = true;
  },

  resumeGame: function () {
	var rect = new Phaser.Rectangle().copyFrom(this.resumeButton);
	if (rect.contains(this.game.input.x + 100, this.game.input.y)) {
		this.resumeButton.visible = false;
		this.quitButton.visible = true;
		this.pauseButton.visible = true;
		this.game.input.onDown.remove(this.resumeGame, this);
		this.game.paused = false;
	}
  }

};
