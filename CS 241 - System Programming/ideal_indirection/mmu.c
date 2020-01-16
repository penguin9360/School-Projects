/**
* Ideal Indirection
* CS 241 - Fall 2019
*/
# include "mmu.h"
# include <assert.h>
# include <dirent.h>
# include <stdio.h>
# include <string.h>
# include <unistd.h>

void set_page_dir_entry(page_directory_entry * dir_entry, 
                        uint32_t base_addr, uint32_t present, uint32_t accessed, uint32_t read_write, uint32_t user_supervisor);
                        
void set_table_entry(page_table_entry * table_entry, 
                        uint32_t base_addr, uint32_t present, uint32_t accessed, uint32_t read_write, uint32_t user_supervisor);

mmu * mmu_create() {
    mmu * my_mmu = calloc(1, sizeof(mmu));
    my_mmu->tlb = tlb_create();
    return my_mmu;
}

void set_page_dir_entry(page_directory_entry * dir_entry, 
                        uint32_t base_addr, uint32_t present, uint32_t accessed, uint32_t read_write, uint32_t user_supervisor) {
        dir_entry->base_addr = base_addr;
        dir_entry->present = present;
        dir_entry->accessed = accessed;
        dir_entry->read_write = read_write;
        dir_entry->user_supervisor = user_supervisor;
}

void set_table_entry(page_table_entry * table_entry, 
                        uint32_t base_addr, uint32_t present, uint32_t accessed, uint32_t read_write, uint32_t user_supervisor) {
        table_entry->base_addr = base_addr;
        table_entry->present = present;
        table_entry->accessed = accessed;
        table_entry->read_write = read_write;
        table_entry->user_supervisor = user_supervisor;
}

void mmu_read_from_virtual_address(mmu * this, addr32 virtual_address,
size_t pid, void * buffer, size_t num_bytes) {
    assert(this);
    assert(pid < MAX_PROCESS_ID);
    assert(num_bytes + (virtual_address % PAGE_SIZE) <= PAGE_SIZE);
    // TODO: Implement me!

    addr32 offsets1 = virtual_address >> 22;
    // last 10 bits
    addr32 pte_index_offset = (virtual_address >> 12) & 0x3ff;
    addr32 to_physical_offset = virtual_address & 0xfff;
    if (this->curr_pid != pid) {
        tlb_flush( & (this->tlb));
        this->curr_pid = pid;
        page_directory * page_dir = this->page_directories[pid];
        if (!address_in_segmentations(this->segmentations[pid], virtual_address)) {
            mmu_raise_segmentation_fault(this);
            return;
        }
        vm_segmentation * vm_seg = find_segment(this->segmentations[pid], virtual_address);
        mmu_tlb_miss(this);
        if (!((page_dir->entries[offsets1]).present)) {
            mmu_raise_page_fault(this);
            page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
            set_page_dir_entry(dir_entry, (ask_kernel_for_frame(NULL) >> NUM_OFFSET_BITS), true, true, true, true);  
            mmu_raise_page_fault(this);
            page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
            page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
            addr32 sys_addr = physical_addr + to_physical_offset;
            memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            return;
        }
        page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
        page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
        if (!((p_table->entries[pte_index_offset]).present)) {
            mmu_raise_page_fault(this);
            page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
            if (get_system_pointer_from_pte(table_entry)) {
                read_page_from_disk(table_entry);
                table_entry->present = true;
                addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
                tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
                memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
                return;
            }
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
            addr32 sys_addr = physical_addr + to_physical_offset;
            memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            return;
        }
        page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
        addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
        return;
    }
    page_directory * page_dir = this->page_directories[pid];
    if (!address_in_segmentations(this->segmentations[pid], virtual_address)) {
        mmu_raise_segmentation_fault(this);
        return;
    }
    vm_segmentation * vm_seg = find_segment(this->segmentations[pid], virtual_address);
    page_table_entry * table_entry = tlb_get_pte( & (this->tlb), virtual_address & 0xfffff000);
    if (table_entry != NULL) {
        if (!table_entry->present) {
            mmu_raise_page_fault(this);
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
 
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);

            read_page_from_disk(table_entry);
            addr32 sys_addr = physical_addr + to_physical_offset;
            memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
            return;
        }
        addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
        memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
        return;
    }
    mmu_tlb_miss(this);
    if (!((page_dir->entries[offsets1]).present)) {
        mmu_raise_page_fault(this);
        page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);     
        set_page_dir_entry(dir_entry, (ask_kernel_for_frame(NULL) >> NUM_OFFSET_BITS), true, true, true, true);
        mmu_raise_page_fault(this);
        page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
        page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
        addr32 physical_addr = ask_kernel_for_frame(table_entry);
        set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
        addr32 sys_addr = physical_addr + to_physical_offset;
        memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        return;
    }
    page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
    page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
    if (!((p_table->entries[pte_index_offset]).present)) {
        mmu_raise_page_fault(this);
        table_entry =  & (p_table->entries[pte_index_offset]);
        if (get_system_pointer_from_pte(table_entry)) {
            read_page_from_disk(table_entry);
            table_entry->present = true;
            addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
            return;
        }
        addr32 physical_addr = ask_kernel_for_frame(table_entry);
        set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
        addr32 sys_addr = physical_addr + to_physical_offset;
        memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        return;
    }
    table_entry =  & (p_table->entries[pte_index_offset]);
    addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
    memmove(buffer, get_system_pointer_from_address(sys_addr), num_bytes);
    return;
}

void mmu_write_to_virtual_address(mmu * this, addr32 virtual_address, size_t pid,
const void * buffer, size_t num_bytes) {
    assert(this);
    assert(pid < MAX_PROCESS_ID);
    assert(num_bytes + (virtual_address % PAGE_SIZE) <= PAGE_SIZE);
    // TODO: Implement me!
    addr32 offsets1 = virtual_address >> 22;
    addr32 pte_index_offset = (virtual_address >> 12) & 0x3ff;
    addr32 to_physical_offset = virtual_address & 0xfff;
    if (this->curr_pid != pid) {
        tlb_flush( & (this->tlb));
        this->curr_pid = pid;
        page_directory * page_dir = this->page_directories[pid];
        if (!address_in_segmentations(this->segmentations[pid], virtual_address)) {
            mmu_raise_segmentation_fault(this);
            return;
        }
        vm_segmentation * vm_seg = find_segment(this->segmentations[pid], virtual_address);
        if (!((vm_seg->permissions) & WRITE)) {
            mmu_raise_segmentation_fault(this);
            return;
        }
        mmu_tlb_miss(this);
        if (!((page_dir->entries[offsets1]).present)) {
            mmu_raise_page_fault(this);
            page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);     
            set_page_dir_entry(dir_entry, (ask_kernel_for_frame(NULL) >> NUM_OFFSET_BITS), true, true, true, true); 
            mmu_raise_page_fault(this);
            page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
            page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);

            table_entry->dirty = true;

            addr32 sys_addr = physical_addr + to_physical_offset;
            memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            return;
        }
        page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
        page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
        if (!((p_table->entries[pte_index_offset]).present)) {
            mmu_raise_page_fault(this);
            page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
            if (get_system_pointer_from_pte(table_entry)) {
                read_page_from_disk(table_entry);
                addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
                table_entry->present = true;
                tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
                memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
                return;
            }
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
            table_entry->dirty = true;
            addr32 sys_addr = physical_addr + to_physical_offset;
            memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            return;
        }
        page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
        addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
        return;
    }
    page_directory * page_dir = this->page_directories[pid];
    if (!address_in_segmentations(this->segmentations[pid], virtual_address)) {
        mmu_raise_segmentation_fault(this);
        return;
    }
    vm_segmentation * vm_seg = find_segment(this->segmentations[pid], virtual_address);
    if (!((vm_seg->permissions) & WRITE)) {
        mmu_raise_segmentation_fault(this);
        return;
    }
    page_table_entry * table_entry = tlb_get_pte( & (this->tlb), virtual_address & 0xfffff000);
    if (table_entry != NULL) {
        if (!table_entry->present) {
            mmu_raise_page_fault(this);
            addr32 physical_addr = ask_kernel_for_frame(table_entry);
            set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
            table_entry->dirty = true;
            
            read_page_from_disk(table_entry);
            addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
            memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
            return;
        }
        addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
        memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
        return;
    }
    mmu_tlb_miss(this);
    if (!((page_dir->entries[offsets1]).present)) {
        mmu_raise_page_fault(this);
        page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
        set_page_dir_entry(dir_entry, (ask_kernel_for_frame(NULL) >> NUM_OFFSET_BITS), true, true, true, true); 
        mmu_raise_page_fault(this);
        page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
        page_table_entry * table_entry =  & (p_table->entries[pte_index_offset]);
        addr32 physical_addr = ask_kernel_for_frame(table_entry);
        set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
        table_entry->dirty = true;
        
        addr32 sys_addr = physical_addr + to_physical_offset;
        memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        return;
    }
    page_directory_entry * dir_entry =  & (page_dir->entries[offsets1]);
    page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
    if (!((p_table->entries[pte_index_offset]).present)) {
        mmu_raise_page_fault(this);
        table_entry =  & (p_table->entries[pte_index_offset]);
        if (get_system_pointer_from_pte(table_entry)) {
            read_page_from_disk(table_entry);
            table_entry->present = true;
            addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
            tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
            memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
            return;
        }
        addr32 physical_addr = ask_kernel_for_frame(table_entry);
        set_table_entry(table_entry, (physical_addr >> NUM_OFFSET_BITS), true, true, (vm_seg->permissions) & WRITE, true);
        table_entry->dirty = true;
        addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
        memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
        tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
        return;
    }
    table_entry =  & (p_table->entries[pte_index_offset]);
    addr32 sys_addr = ((table_entry->base_addr << 12) + to_physical_offset);
    tlb_add_pte( & (this->tlb), virtual_address & 0xfffff000, table_entry);
    memmove(get_system_pointer_from_address(sys_addr), buffer, num_bytes);
    return;
}

void mmu_tlb_miss(mmu * this) {
    this->num_tlb_misses++;
}

void mmu_raise_page_fault(mmu * this) {
    this->num_page_faults++;
}

void mmu_raise_segmentation_fault(mmu * this) {
    this->num_segmentation_faults++;
}

void mmu_add_process(mmu * this, size_t pid) {
    assert(pid < MAX_PROCESS_ID);
    addr32 page_directory_address = ask_kernel_for_frame(NULL);
    this->page_directories[pid] =
    (page_directory*)get_system_pointer_from_address(
    page_directory_address);
    page_directory * page_dir = this->page_directories[pid];
    this->segmentations[pid] = calloc(1, sizeof(vm_segmentations));
    vm_segmentations * segmentations = this->segmentations[pid];

    // Note you can see this information in a memory map by using
    // cat /proc/self/maps
    segmentations->segments[STACK] =
    (vm_segmentation) {
        .start = 0xBFFFE000,
        .end = 0xC07FE000, // 8mb stack
        .permissions = READ | WRITE,
        .grows_down = true
    };

    segmentations->segments[MMAP] =
    (vm_segmentation) {
        .start = 0xC07FE000,
        .end = 0xC07FE000,
        // making this writeable to simplify the next lab.
        // todo make this not writeable by default
        .permissions = READ | EXEC | WRITE,
        .grows_down = true
    };

    segmentations->segments[HEAP] =
    (vm_segmentation) {
        .start = 0x08072000,
        .end = 0x08072000,
        .permissions = READ | WRITE,
        .grows_down = false
    };

    segmentations->segments[BSS] =
    (vm_segmentation) {
        .start = 0x0805A000,
        .end = 0x08072000,
        .permissions = READ | WRITE,
        .grows_down = false
    };

    segmentations->segments[DATA] =
    (vm_segmentation) {
        .start = 0x08052000,
        .end = 0x0805A000,
        .permissions = READ | WRITE,
        .grows_down = false
    };

    segmentations->segments[TEXT] =
    (vm_segmentation) {
        .start = 0x08048000,
        .end = 0x08052000,
        .permissions = READ | EXEC,
        .grows_down = false
    };

    // creating a few mappings so we have something to play with (made up)
    // this segment is made up for testing purposes
    segmentations->segments[TESTING] =
    (vm_segmentation) {
        .start = PAGE_SIZE,
        .end = 3 * PAGE_SIZE,
        .permissions = READ | WRITE,
        .grows_down = false
    };
    // first 4 mb is bookkept by the first page directory entry
    page_directory_entry * dir_entry =  & (page_dir->entries[0]);
    // assigning it a page table and some basic permissions
    dir_entry->base_addr = (ask_kernel_for_frame(NULL) >> NUM_OFFSET_BITS);
    dir_entry->present = true;
    dir_entry->read_write = true;
    dir_entry->user_supervisor = true;

    // setting entries 1 and 2 (since each entry points to a 4kb page)
    // of the page table to point to our 8kb of testing memory defined earlier
    for (int i = 1; i < 3; i++) {
        page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
        page_table_entry * table_entry =  & (p_table->entries[i]);
        table_entry->base_addr = (ask_kernel_for_frame(table_entry) >> NUM_OFFSET_BITS);
        table_entry->present = true;
        table_entry->read_write = true;
        table_entry->user_supervisor = true;
    }
}

void mmu_remove_process(mmu * this, size_t pid) {
    assert(pid < MAX_PROCESS_ID);
    // example of how to BFS through page table tree for those to read code.
    page_directory * page_dir = this->page_directories[pid];
    if (page_dir) {
        for (size_t vpn1 = 0; vpn1 < NUM_ENTRIES; vpn1++) {
            page_directory_entry * dir_entry =  & (page_dir->entries[vpn1]);
            if (dir_entry->present) {
                page_table * p_table = (page_table*)get_system_pointer_from_pde(dir_entry);
                for (size_t vpn2 = 0; vpn2 < NUM_ENTRIES; vpn2++) {
                    page_table_entry * table_entry =  & (p_table->entries[vpn2]);
                    if (table_entry->present) {
                        void * frame = (void*)get_system_pointer_from_pte(table_entry);
                        return_frame_to_kernel(frame);
                    }
                    remove_swap_file(table_entry);
                }
                return_frame_to_kernel(p_table);
            }
        }
        return_frame_to_kernel(page_dir);
    }

    this->page_directories[pid] = NULL;
    free(this->segmentations[pid]);
    this->segmentations[pid] = NULL;

    if (this->curr_pid == pid) {
        tlb_flush( & (this->tlb));
    }
}

void mmu_delete(mmu * this) {
    for (size_t pid = 0; pid < MAX_PROCESS_ID; pid++) {
        mmu_remove_process(this, pid);
    }

    tlb_delete(this->tlb);
    free(this);
    remove_swap_files();
}