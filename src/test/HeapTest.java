package test;

import util.queue.KAryHeap;
import java.util.Arrays;
import util.queue.PriorityQueue;
import util.queue.SoftPriorityQueue;
import util.queue.SoftHeap;

public class HeapTest {

    public static void main(String[] args) {
        PriorityQueue<Integer> pq = KAryHeap.naturallyOrdered(2);
        pq.insert(5);
        pq.insert(7);
        pq.insert(2);
//        System.out.println(pq.pop());
//        System.out.println(pq.pop());
//        System.out.println(pq.pop());

        SoftPriorityQueue<Integer> lpq = SoftHeap.naturallyOrdered(0.25);

        int[] elements = {104, 191, 180, 70, 523, 84, 493, 270, 700, 626, 284, 704, 290, 612, 240, 92, 918, 944, 263, 873, 620, 153, 174, 615, 940, 783, 21, 213, 364, 429, 755, 613, 438, 216, 903, 294, 545, 281, 134, 325, 342, 983, 189, 995, 116, 393, 455, 766, 963, 845, 228, 396, 379, 223, 474, 814, 0, 913, 36, 728, 33, 374, 114, 929, 299, 96, 329, 970, 915, 156, 528, 382, 789, 834, 5, 359, 661, 154, 718, 155, 606, 797, 522, 63, 158, 276, 39, 886, 319, 69, 19, 352, 457, 799, 188, 986, 511, 674, 315, 891, 64, 758, 578, 917, 884, 717, 68, 428, 320, 803, 128, 732, 347, 258, 28, 860, 842, 169, 401, 323, 150, 362, 767, 619, 442, 608, 880, 514, 108, 712, 56, 419, 262, 78, 483, 222, 7, 798, 853, 436, 894, 561, 786, 430, 38, 582, 238, 465, 502, 650, 686, 989, 598, 124, 927, 532, 205, 259, 366, 719, 207, 664, 469, 574, 663, 301, 601, 878, 434, 49, 964, 576, 490, 167, 410, 859, 821, 675, 552, 476, 525, 29, 539, 373, 413, 202, 367, 695, 981, 669, 943, 603, 23, 197, 984, 215, 74, 592, 388, 702, 494, 727, 304, 967, 503, 131, 384, 920, 132, 811, 246, 971, 856, 730, 311, 11, 495, 196, 307, 59, 282, 908, 376, 277, 816, 371, 192, 833, 509, 906, 879, 679, 479, 122, 840, 16, 591, 179, 358, 636, 115, 835, 560, 905, 162, 683, 759, 273, 305, 699, 98, 472, 922, 924, 861, 378, 781, 357, 611, 754, 985, 936, 784, 201, 338, 910, 13, 715, 125, 542, 255, 610, 446, 668, 326, 749, 81, 526, 843, 100, 958, 616, 550, 698, 551, 112, 123, 567, 414, 765, 529, 941, 857, 143, 945, 822, 558, 193, 969, 48, 99, 47, 946, 694, 2, 177, 345, 662, 317, 175, 449, 643, 274, 595, 109, 365, 774, 691, 57, 235, 199, 568, 580, 826, 546, 793, 232, 173, 585, 130, 212, 50, 26, 684, 487, 77, 275, 682, 530, 279, 779, 623, 412, 171, 297, 466, 283, 372, 349, 796, 184, 370, 763, 795, 837, 693, 678, 322, 808, 209, 630, 792, 71, 107, 562, 688, 851, 955, 94, 870, 617, 836, 658, 535, 632, 280, 852, 383, 801, 959, 772, 785, 117, 982, 823, 190, 874, 340, 999, 110, 66, 825, 411, 139, 575, 631, 269, 705, 646, 204, 871, 397, 565, 377, 300, 790, 432, 106, 581, 22, 168, 501, 980, 579, 641, 221, 899, 553, 854, 923, 62, 714, 313, 925, 467, 696, 689, 87, 83, 736, 144, 408, 337, 260, 178, 306, 335, 607, 960, 249, 978, 887, 541, 974, 588, 889, 671, 431, 416, 512, 418, 527, 519, 166, 135, 559, 458, 827, 37, 389, 242, 812, 237, 331, 407, 54, 921, 534, 271, 998, 464, 423, 521, 656, 762, 198, 948, 965, 741, 748, 447, 544, 933, 15, 710, 596, 847, 324, 609, 570, 976, 752, 647, 468, 118, 543, 506, 327, 584, 386, 492, 777, 409, 161, 369, 972, 285, 890, 341, 157, 85, 987, 667, 40, 211, 402, 103, 425, 101, 634, 165, 163, 72, 810, 934, 711, 31, 577, 805, 267, 295, 41, 206, 735, 236, 344, 265, 883, 292, 427, 849, 649, 794, 642, 703, 287, 725, 872, 12, 361, 385, 51, 420, 303, 422, 737, 272, 707, 807, 973, 316, 140, 406, 761, 868, 470, 677, 734, 346, 187, 245, 813, 381, 129, 600, 75, 451, 571, 32, 435, 885, 844, 621, 640, 89, 478, 952, 8, 254, 931, 394, 42, 91, 988, 809, 517, 938, 151, 1, 977, 866, 404, 268, 563, 644, 738, 405, 507, 919, 992, 787, 308, 720, 993, 896, 791, 226, 877, 855, 186, 556, 916, 815, 824, 264, 496, 93, 589, 172, 61, 14, 454, 111, 67, 302, 4, 24, 513, 444, 864, 443, 586, 587, 692, 881, 602, 219, 893, 773, 497, 869, 484, 328, 121, 485, 391, 746, 195, 79, 462, 723, 80, 895, 516, 250, 939, 659, 473, 750, 60, 838, 505, 355, 448, 127, 20, 360, 234, 676, 538, 666, 504, 716, 828, 248, 229, 181, 770, 524, 203, 975, 935, 665, 597, 583, 850, 257, 771, 350, 35, 733, 540, 9, 343, 776, 321, 907, 942, 52, 218, 639, 572, 491, 882, 697, 82, 387, 593, 486, 548, 653, 701, 368, 10, 225, 536, 45, 912, 46, 289, 400, 348, 298, 614, 440, 164, 537, 726, 756, 740, 875, 44, 339, 713, 334, 159, 902, 932, 590, 817, 145, 830, 160, 251, 146, 832, 743, 898, 645, 333, 456, 499, 953, 928, 722, 635, 95, 637, 841, 956, 399, 119, 937, 627, 25, 624, 858, 53, 17, 863, 831, 296, 65, 739, 194, 979, 450, 685, 256, 76, 353, 136, 481, 102, 460, 950, 148, 113, 518, 239, 475, 622, 508, 137, 742, 680, 73, 775, 480, 217, 227, 253, 417, 800, 926, 6, 147, 657, 744, 463, 557, 510, 90, 757, 651, 318, 261, 769, 390, 395, 625, 961, 904, 252, 819, 183, 482, 149, 220, 182, 88, 876, 865, 531, 142, 433, 804, 214, 208, 708, 654, 30, 788, 660, 86, 724, 141, 618, 867, 729, 745, 288, 176, 231, 888, 520, 498, 224, 968, 954, 706, 806, 997, 266, 930, 241, 58, 569, 747, 839, 445, 731, 375, 332, 628, 648, 105, 782, 991, 573, 18, 437, 966, 547, 55, 247, 278, 681, 200, 133, 633, 354, 293, 461, 780, 312, 709, 210, 310, 605, 3, 421, 244, 97, 488, 309, 996, 314, 949, 120, 760, 566, 356, 515, 138, 43, 670, 330, 802, 489, 392, 403, 768, 690, 363, 990, 897, 599, 951, 957, 233, 243, 533, 351, 947, 126, 549, 914, 892, 398, 424, 27, 426, 230, 673, 638, 452, 901, 962, 911, 415, 846, 672, 185, 820, 721, 500, 778, 751, 459, 594, 829, 477, 900, 753, 862, 629, 687, 453, 655, 286, 471, 170, 291, 336, 439, 848, 555, 909, 152, 34, 604, 764, 380, 554, 564, 652, 818, 441, 994};
        for (int i : elements) {
            lpq.insert(i);
        }
        for (int i = 0; i < elements.length; i++) {
        	elements[i] = lpq.pop();
        }
        System.out.println(Arrays.toString(elements));
    }
}
