Interface = function (game, player, HUD) {
    this.game = game;
    this.player = player;
    this.HUD = HUD;
    this.highScore = 0;
};

Interface.prototype.constructor = Interface;

// check player properties boundaries and win and lose condition
Interface.prototype.checkPlayer = function () {
    this.player.score = this.checkNumMin(this.player.score);
    this.player.fat = this.checkNumMin(this.player.fat);
    this.player.muscle = this.checkNumMin(this.player.muscle);
    if(this.player.energy > 1000){
        this.player.energy = 1000;
    }
    else if(this.player.energy < 1){
        this.player.energy = 0;
        this.game.gameOver();
    }
};

// check a number whether less than zero
Interface.prototype.checkNumMin = function (num){
    if(num < 0)
        return 0;
    else
        return num;
};

// update player properites and display them on screen
Interface.prototype.update = function (){
    // update stat
    this.score = this.player.score;
    this.energy = this.player.energy;
    this.fat = this.player.fat;
    this.muscle = this.player.muscle;
    this.highScore = this.highestRecord(this.score, this.highScore);
    // display text
    this.HUD.updateText({score: this.score, energy: this.energy, fat: this.fat, muscle: this.muscle, highScore: this.highScore});
    
};

Interface.prototype.addHiScore = function (score){
    for(i = 0;i < highScoreRecord.length; i++){
        if(score > highScoreRecord[i]){
            highScoreRecord.pop();
            highScoreRecord.splice(i, 0, score);
            return 0;
        }
    }
};

Interface.prototype.highestRecord = function (current, record){
    if(current > record){
        return current;
    }
    return record;
};