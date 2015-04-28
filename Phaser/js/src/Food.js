Food = function (game, player, context) {
    this.game = game;
    this.player = player;
    this.context = context;

    this.create();
};

Food.prototype = Object.create(Phaser.Sprite.prototype);
Food.prototype.constructor = Food;

Food.prototype.create = function () {
    
    Phaser.Sprite.call(this, this.game, this.context.position.x, this.context.position.y, foodImgKey, this.context.img_id);
    this.game.physics.enable(this, Phaser.Physics.ARCADE);
    //this.anchor.setTo(0, 1);
    
    this.game.add.existing(this);
};

Food.prototype.appear = function () {
	this.reset(this.game.world.randomX, 0);
    this.body.velocity.setTo(this.context.velocity.x,this.context.velocity.y);
    this.body.gravity.setTo(this.context.gravity.x,this.context.gravity.y);
};

Food.prototype.effect = function () {
    this.player.score += this.context.score * (this.player.doubleScore == true ? 2 : 1);
    this.player.energy += this.context.energy;
    this.player.fat += this.context.fat;
    this.player.muscle += this.context.muscle;
	this.kill();
};
