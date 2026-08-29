package slimeknights.mantle.client.screen.book.element;

/** A book element that occupies a rectangle and can therefore be hovered and clicked */
public abstract class SizedBookElement extends BookElement {
  public int width;
  public int height;

  public SizedBookElement(int x, int y, int width, int height) {
    super(x, y);
    this.width = width;
    this.height = height;
  }

  @Override
  public boolean isHovered(double mouseX, double mouseY) {
    return mouseX >= this.x && mouseX < this.x + this.width
        && mouseY >= this.y && mouseY < this.y + this.height;
  }
}
