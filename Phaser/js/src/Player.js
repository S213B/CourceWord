Player = function (game) {
    // instance properties
    this.score = 0;
    this.speed = PLAYER.initSpeed;
    this.energy = PLAYER.fullEnergy;
    this.fat = 0;
    this.muscle = 0;
    this.game = game;
    this.doubleScore = false;   
    
    // inherit from super class Phaser.Sprite
    Phaser.Sprite.call(this, game, game.world.centerX, PLAYER.initPos.y, PLAYER.img);
    
    // enable game physics
    game.physics.enable(this, Phaser.Physics.ARCADE);
    this.anchor.setTo(0.5, 1);    
    this.body.velocity.y = 100;
    
    // Player walking animations
    this.animations.add('l', [14], 1);
    this.animations.add('r', [15], 1);
    this.animations.add('left', [13, 12, 11, 10, 14], 6, false);
    this.animations.add('right', [16, 17, 18, 19, 15], 6, false);
    this.animations.add('left_dash', [4, 3, 2, 1, 0], 5, false);
    this.animations.add('right_dash', [5, 6, 7, 8, 15], 5, false);
    
    game.add.existing(this);
};

Player.prototype = Object.create(Phaser.Sprite.prototype);
Player.prototype.constructor = Player;

// methods
Player.prototype.restart = function () {
	this.x = this.game.world.centerX;
    this.body.velocity.setTo(0, 0);
    this.score = 0;
    this.energy = 1000;
    this.fat = 0;
    this.muscle = 0;
    this.speed = 500;
    this.frame = 0;
	this.scale.x = 1;
	this.scale.y = 1;
};

Player.prototype.walk = function (facing){
    this.energy -= (this.fat+1)/100;
    this.fatToMuscle(0.03);
    this.body.velocity.x = this.speed * ((this.muscle+1)/(this.fat+1)) * (facing == 'left' ? -1 : 1);
    this.animations.play(facing);
};

Player.prototype.dash = function (facing){   
    this.energy -= (this.fat+1)/20;
    this.fatToMuscle(0.05);
    this.body.velocity.x = 2200 * ((this.muscle+1)/(this.fat+1)) * (facing == 'left_dash' ? -1 : 1);
    this.animations.play(facing);     
};

Player.prototype.stand = function (){    
    this.energy -= (this.fat+1)/500;
    this.muscle -= 0.1;
    this.body.velocity.x = 0;
};

// transform fat into muscle or vice versa
Player.prototype.fatToMuscle = function (amount){
    if(this.fat > 0){
        this.fat -= amount;
        this.muscle += amount;
    }
};
