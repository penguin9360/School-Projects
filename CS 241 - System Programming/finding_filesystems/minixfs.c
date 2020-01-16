/**
* Finding Filesystems
* CS 241 - Fall 2019
*/
#include "minixfs.h"
#include "minixfs_utils.h"
#include <errno.h>
#include <stdio.h>
#include <string.h>


/**
* Virtual paths:
*  Add your new virtual endpoint to minixfs_virtual_path_names
*/
char *minixfs_virtual_path_names[] = {"info", /* add your paths here*/};

/**
* Forward declaring block_info_string so that we can attach unused on it
* This prevents a compiler warning if you haven't used it yet.
*
* This function generates the info string that the virtual endpoint info should
* emit when read
*/
static char *block_info_string(ssize_t num_used_blocks) __attribute__((unused));
static char *block_info_string(ssize_t num_used_blocks) {
	char *block_string = NULL;
	ssize_t curr_free_blocks = DATA_NUMBER - num_used_blocks;
	asprintf(&block_string, "Free blocks: %zd\n"
	"Used blocks: %zd\n",
	curr_free_blocks, num_used_blocks);
	return block_string;
}

void get_clock_write(inode* to_write);
size_t find_minimum(size_t a, size_t b);
void increment_size(inode* to_write, size_t count, off_t *off);
void update_buf(char* blk, file_system *fs, data_block_number* block_id, char* buf_, int remain);

void update_buf(char* blk, file_system *fs, data_block_number* block_id, char* buf_, int remain) {
	blk=(char*)(fs->data_root+*block_id);
	memcpy(blk,buf_,find_minimum(remain,(int)sizeof(data_block)));
	buf_+=find_minimum(remain,(int)sizeof(data_block));
	remain-=find_minimum(remain,(int)sizeof(data_block));
	block_id++;
}

size_t find_minimum(size_t a, size_t b) {
	if (a < b) {
		return a;
	} else {
		return b;
	}
}

void get_clock_write(inode* to_write) {
	clock_gettime(CLOCK_REALTIME, &to_write->atim);
	clock_gettime(CLOCK_REALTIME, &to_write->mtim);
}

void increment_size(inode* to_write, size_t count, off_t *off) {
	to_write->size=*off+count;
	*off=*off+count;
	get_clock_write(to_write);
}



// Don't modify this line unless you know what you're doing
int minixfs_virtual_path_count =
sizeof(minixfs_virtual_path_names) / sizeof(minixfs_virtual_path_names[0]);

int minixfs_chmod(file_system *fs, char *path, int new_permissions) {
	// Thar she blows!
	new_permissions = new_permissions | 0777;
	inode * i = get_inode(fs, path);
	if(!i){
		errno = ENOENT;
		return -1;
	}
	i->mode = i->mode | new_permissions;
	clock_gettime(CLOCK_REALTIME, &i->ctim);
	return 0;
}

int minixfs_chown(file_system *fs, char *path, uid_t owner, gid_t group) {
	// Land ahoy!
	inode* i=get_inode(fs,path);
	if(!i){
		errno=ENOENT;
		return -1;
	}
	if(owner!=(uid_t)-1) {
		i->uid = owner;
	}
	if(group != (gid_t)-1) {
		i->gid = group;
	}
	clock_gettime(CLOCK_REALTIME, &i->ctim);
	return 0;

}

inode *minixfs_create_inode_for_path(file_system *fs, const char *path) {
	// Land ahoy!

	inode_number i_num = first_unused_inode(fs);
	if(get_inode(fs,path) != NULL || i_num == -1){
		return NULL;
	}
	char* file_name = NULL;
	
	if(!valid_filename(file_name)) {
		return NULL;
	}
	
	inode* parent_dir = parent_directory(fs,path,(const char**) &file_name);
	
	if((parent_dir->mode & 0700) != 0700) {
		return NULL;
	}
	char block[FILE_NAME_LENGTH+INODE_SIZE];
	minixfs_dirent dirent;
	dirent.name = file_name;
	dirent.inode_num = i_num;
	make_string_from_dirent(block, dirent);
	size_t new_file_len = FILE_NAME_LENGTH + INODE_SIZE;
	
	if(parent_dir->size+new_file_len <= NUM_DIRECT_BLOCKS * 16 * KILOBYTE) {
		int temp = parent_dir->size/(int)sizeof(data_block);
		char* parent_data = (char*)(fs->data_root + parent_dir->direct[temp]);
		int remain = parent_dir->size % (int)sizeof(data_block);
		if(!remain) {
			data_block_number if_available = first_unused_data(fs);
			if(if_available == -1) {
				return NULL;
			}
			parent_dir->direct[temp]=if_available;
			set_data_used(fs,if_available,1);
			parent_data = (char*)(fs->data_root + if_available);
			memcpy(parent_data,block,new_file_len);
			
		} else {
			memcpy(parent_data + remain,block,new_file_len);  
		}
		parent_dir->size += new_file_len;
	} else if(parent_dir->size+new_file_len <= NUM_DIRECT_BLOCKS * 16 * KILOBYTE+NUM_DIRECT_BLOCKS * 16 * KILOBYTE) {
		data_block_number d_b_n = parent_dir->size / (int)sizeof(data_block) - NUM_DIRECT_BLOCKS;
		if(parent_dir->indirect == UNASSIGNED_NODE) {
			data_block_number first_idx = first_unused_data(fs);
			
			if(first_idx==-1) {
				return NULL;
			}
			set_data_used(fs,first_idx,1);
			parent_dir->indirect=first_idx;
			data_block_number * idx_self=(data_block_number*)(fs->data_root + first_idx);
			data_block_number index_2=first_unused_data(fs);
			if(index_2==-1) {
				return NULL;
			}
			set_data_used(fs,index_2,1);
			idx_self[0]=index_2;
			char* id_data_p=(char*)(fs->data_root+index_2);
			memcpy(id_data_p,block,new_file_len);
		} else {
			data_block_number* idx_self=(data_block_number*)(fs->data_root+parent_dir->indirect);
			if(parent_dir->size%(int)sizeof(data_block)) {
				char* id_data_p=(char*)(fs->data_root+idx_self[d_b_n]);
				memcpy(id_data_p,block,new_file_len);
			} else {
				data_block_number index_2=first_unused_data(fs);
				if(index_2==-1) {
					return NULL;
				}
				set_data_used(fs,index_2,1);
				idx_self[d_b_n]=index_2;
				char* id_data_p=(char*)(fs->data_root+index_2);
				memcpy(id_data_p,block,new_file_len);
			}
		}
		parent_dir->size+=new_file_len;
		
	} else {
		return NULL;  
	}
	
	inode* newNode=fs->inode_root+i_num;
	init_inode(parent_dir, newNode);
	return newNode;
	
}

ssize_t minixfs_virtual_read(file_system *fs, const char *path, void *buf,
size_t count, off_t *off) {
	if (!strcmp(path, "info")) {
		// TODO implement the "info" virtual file here
		int i=0;
		int used=0;
		for(i = 0; i < DATA_NUMBER; i++){
			if(get_data_used(fs,i))	{
				used++;
			}
		}
		
		char* str = block_info_string(used);
		size_t len = strlen(str) - *off;
		memcpy(buf, str + *off, find_minimum(count,len));
		*off+=find_minimum(count,len);
		return find_minimum(count,len); 
	}
	// TODO implement your own virtual file here
	errno = ENOENT;
	return -1;
}

ssize_t minixfs_write(file_system *fs, const char *path, const void *buf,
size_t count, off_t *off) {
	// X marks the spot
	if(*off+count>NUM_DIRECT_BLOCKS*16 * KILOBYTE+NUM_DIRECT_BLOCKS*16 * KILOBYTE){
		errno=ENOSPC;
		return -1;
	}
	uint64_t num_free=0;
	int i=0;
	for(i=0;i<DATA_NUMBER;i++){
		if(!get_data_used(fs,i)){
			num_free++;
		}
	}
	inode* to_write=get_inode(fs,path);
	
	if(!to_write){
		to_write=minixfs_create_inode_for_path(fs,path);
		if(to_write==NULL)
		return -1;
	}
	if(to_write->size<*off+count){
		int block_count=(*off+count)/(int)sizeof(data_block);
		if((*off+count)%(int)sizeof(data_block)){
			block_count++;
		}

		if(minixfs_min_blockcount(fs,path,block_count)==-1) {
			return -1;
		}
	}
	i=0;
	int start_idx=*off/(int)sizeof(data_block);
	int init=*off-start_idx*(int)sizeof(data_block);
	int remain=count;
	char* blk=NULL;
	if(start_idx<NUM_DIRECT_BLOCKS){
		int read_dir=to_write->direct[start_idx];
		blk=(char*)(fs->data_root+read_dir);
		char* buf_=(char*)buf;
		if(count+init<=(int)sizeof(data_block))	{
			memcpy(blk+init,buf,count);
			increment_size(to_write, count, off);

			return count;
		} else {
			int temp_int=(int)sizeof(data_block)-init;
			memcpy(blk+init,buf,temp_int);
			remain-=temp_int;
			start_idx++;
			buf_+=temp_int;
		}
		
		
		
		while(start_idx<NUM_DIRECT_BLOCKS&&remain>0) {
			read_dir = to_write->direct[start_idx];
			blk =(char*)(fs->data_root+read_dir);
			memcpy(blk,buf_,find_minimum(remain,(int)sizeof(data_block)));
			buf_ += find_minimum(remain,(int)sizeof(data_block));
			remain-=find_minimum(remain,(int)sizeof(data_block));
			start_idx++;
		}
		if(remain<=0)	{
			increment_size(to_write, count, off);
			return count;
		}
		data_block_number* block_id=(int*)(fs->data_root+to_write->indirect);
		
		
		while(remain>0)	{
			update_buf(blk, fs, block_id, buf_, remain);
		}
		increment_size(to_write, count, off);
		return count;
		
	} else {
		data_block_number* block_id=(int*)(fs->data_root+to_write->indirect);
		char* buf_=(char*)buf;
		int block_off=start_idx-NUM_DIRECT_BLOCKS;
		block_id+=block_off;
		blk=(char*)(fs->data_root+*block_id);
		if(count+init<=(int)sizeof(data_block))	{
			memcpy(blk+init,buf,count);
			increment_size(to_write, count, off);
			return count;
		} else {
			int temp_int=(int)sizeof(data_block)-init;
			memcpy(blk+init,buf,temp_int);
			remain-=temp_int;
			block_id++;
			buf_+=temp_int;  
		} while(remain>0) {
			update_buf(blk, fs, block_id, buf_, remain);
		}
		increment_size(to_write, count, off);
		return count;
		
	}    
	get_clock_write(to_write);

	return count;
}

ssize_t minixfs_read(file_system *fs, const char *path, void *buf, size_t count,
off_t *off) {
	const char *virtual_path = is_virtual_path(path);
	if (virtual_path)
	return minixfs_virtual_read(fs, virtual_path, buf, count, off);
	// 'ere be treasure!
	
	inode* to_read=get_inode(fs,path);
	if(!to_read){
		errno=ENOENT;
		return -1;
	}
	
	int ret=0;
	int start_idx=*off/(int)sizeof(data_block);
	int init=*off-start_idx*(int)sizeof(data_block);
	char* blk=NULL;
	
	if(*off >= (long)to_read->size){
		return 0;
	}
	
	int remain = find_minimum(count,to_read->size-*off);
	
	if(start_idx < NUM_DIRECT_BLOCKS){
		int read_dir = to_read->direct[start_idx];
		blk=(char*)(fs->data_root+read_dir);
		char* buf_=buf;
		if(remain+init<=(int)sizeof(data_block)){
			memcpy(buf,blk+init,remain);
			ret+=remain;
			*off += ret;
			clock_gettime(CLOCK_REALTIME, &to_read->atim);

			return ret;
		} else {
			int temp_int=(int)sizeof(data_block)-init;
			memcpy(buf,blk+init,temp_int);
			remain-=temp_int;
			start_idx++;
			ret+=temp_int;
			buf_+=temp_int;
			
		} while(start_idx<NUM_DIRECT_BLOCKS&&remain>0) {
			read_dir=to_read->direct[start_idx];
			blk=(char*)(fs->data_root+read_dir);
			memcpy(buf_,blk,find_minimum(remain,(int)sizeof(data_block)));
			buf_+=find_minimum(remain,(int)sizeof(data_block));
			remain-=find_minimum(remain,(int)sizeof(data_block));
			start_idx++;
			ret+=find_minimum(remain,(int)sizeof(data_block));
		}
		
		if(remain<=0) {
			*off += ret;
			clock_gettime(CLOCK_REALTIME, &to_read->atim);

			return ret;
		}
		data_block_number* block_id=(int*)(fs->data_root+to_read->indirect);
		
		while(remain>0)	{
			update_buf(blk, fs, block_id, buf_, remain);
			ret+=find_minimum(remain,(int)sizeof(data_block));
		}
		*off += ret;
		clock_gettime(CLOCK_REALTIME, &to_read->atim);

		return ret;
		
	} else {
		data_block_number* block_id=(int*)(fs->data_root+to_read->indirect);
		char* buf_=buf;
		int block_off=start_idx-NUM_DIRECT_BLOCKS;
		block_id+=block_off;
		blk=(char*)(fs->data_root+*block_id);
		if(remain+init<=(int)sizeof(data_block)) {
			memcpy(buf_,blk+init,remain);
			*off += remain;
			clock_gettime(CLOCK_REALTIME, &to_read->atim);

			return remain;
		} else {
			int temp_int=(int)sizeof(data_block)-init;
			memcpy(buf,blk+init,temp_int);
			remain-=temp_int;
			block_id++;
			buf_+=temp_int;  
			ret+=temp_int;
		}
		while(remain>0) {
			update_buf(blk, fs, block_id, buf_, remain);
			ret+=find_minimum(remain,(int)sizeof(data_block));
		}
		*off += ret;
		clock_gettime(CLOCK_REALTIME, &to_read->atim);

		return ret;
	}
	clock_gettime(CLOCK_REALTIME, &to_read->atim);

	return count;
}
