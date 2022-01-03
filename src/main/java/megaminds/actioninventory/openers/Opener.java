package megaminds.actioninventory.openers;

public enum Opener {
	ENTITY {
		@Override
		public BasicOpener create() {
			return new EntityOpener();
		}
	}, BLOCK {
		@Override
		public BasicOpener create() {
			return new BlockOpener();
		}
	}, ITEM {
		@Override
		public BasicOpener create() {
			return new ItemOpener();
		}
	};

	public abstract BasicOpener create();
}