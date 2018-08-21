Note: This code is from when I was a junior in high school. As such, it's not representative of my current abilities (i.e. not as clean or succinct as I could write it now). It's still a very interesting project, but don't take the code quality too seriously!

# Tetris with AI

This is the second version of Tetris with AI that I have made. It utilizes the Slick2D library for user input and drawing to the screen. The game is also playable without AI, if you're into that.

# AI
The AI works by taking the current piece and placing it in all possible rotations and locations. It then analyzes what that placement would do: how many lines would it complete, how high would it be placed, would it leave an overhang, does it further block any open spaces?

Upon deciding the best move, it places the block and then continues.

One potential improvement that could be made is to consider the next available piece when deciding where to place the current piece. Perhaps the two could work together in a way that is better than the locations they'd be placed independently.

# Video
https://www.youtube.com/watch?v=mqpz0zoAkSo

(github won't let me embed it)
