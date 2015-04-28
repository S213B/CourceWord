HUD = function (game) {
    this.displayText;
    var style = { font: "25px Verdana", fill: "#000000", align: "left" };
    this.displayText = game.add.text( 0, 0, "Score: 0\nEnergy: 1000/1000\nFat: 0\nMuscle: 0\nHigh Score: 0", style );
};

HUD.prototype.constructor = HUD;

HUD.prototype.updateText = function (content){
    this.displayText.text = "Score: " + content.score + "\n" +
                            "Energy: " + Math.ceil(content.energy) + "/1000\n" +
                            "Fat: " + Math.ceil(content.fat) + "\n" +
                            "Muscle: " + Math.ceil(content.muscle) + "\n" +
                            "High Score: " + content.highScore;
};
