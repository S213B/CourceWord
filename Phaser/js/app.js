window.onload = function() {

  //  Create your Phaser game and inject it into the gameContainer div.
  var game = new Phaser.Game(800, 600, Phaser.AUTO, 'gameContainer');

  //  Add your game states 
  game.state.add('Boot', BasicGame.Boot);
  game.state.add('Preloader', BasicGame.Preloader);
  game.state.add('MainMenu', BasicGame.MainMenu);
  game.state.add('Game', BasicGame.Game);

  //  Now start from one state (usually Boot)
  game.state.start('Boot');

};
