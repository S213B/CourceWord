DbScore = function (game, player, context) {
    // call super class
    Food.call(this, game, player, context);
	this.body.collideWorldBounds = false;
	this.outOfBoundsKill = true;
};

// inherit from Food
DbScore.prototype = Object.create(Food.prototype);
DbScore.prototype.constructor = DbScore;

DbScore.prototype.appear = function () {
	if(this.player.x > this.game.world.width/2){		
		this.reset(this.game.world.width/2-this.game.world.randomX/4, this.context.position.y);
		this.body.gravity.setTo(-this.context.gravity.x,this.context.gravity.y);
	}else{
		this.reset(this.game.world.width/2+this.game.world.randomX/4, this.context.position.y);
		this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
	}
	this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
};

DbScore.prototype.effect = function () {
	var delayTime = 5;  //5s
    this.kill();
	this.player.doubleScore = true;
    this.game.time.events.add(Phaser.Timer.SECOND * delayTime, this.afterEffect, this);
	this.player.score += this.context.score;
};

DbScore.prototype.afterEffect = function() {
	this.player.doubleScore = false;
	//this.appear();
};
