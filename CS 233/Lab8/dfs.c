int 
dfs(int* tree, int i, int input) {
	if (i >= 127) {
		return -1;
	}
	if (input == tree[i]) {
		return 0;
	}

	int ret = DFS(tree, 2 * i, input);
	if (ret >= 0) {
		return ret + 1;
	}
	ret = DFS(tree, 2 * i + 1, input);
	if (ret >= 0) {
		return ret + 1;
	}
	return ret;
}